package io.potatogun.endlessdead.entity.zombie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Landmine;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.MeleeAttackable;
import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.endlessdead.entity.PenetratorDamagable;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.endlessdead.entity.ai.ApproachTarget;
import io.potatogun.endlessdead.entity.ai.MeleeAttackTarget;
import io.potatogun.endlessdead.entity.component.AutoTargeter;
import io.potatogun.endlessdead.entity.component.MeleeAttackComponent;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.world.World;

/**
 * 좀비 — 플레이어를 따라 자동으로 이동하고 손공격하는 몹
 *
 * @param world    개체가 속한 세계
 * @param name     개체 이름
 * @param x        개체의 처음 X 위치
 * @param y        개체의 처음 Y 위치
 * @param width    가로 크기 (픽셀)
 * @param height   세로 크기 (픽셀)
 * @param settings 좀비 옵션
 */
abstract class Zombie(world: World, name: String, x: Float, y: Float, width: Float, height: Float, settings: Properties) : LivingEntity(world, name, x, y, width, height, settings.health, Textures.getShared("zombie")), MeleeAttackable, DamageListener, PenetratorDamagable, Movable, Targetable {
	private val meleeAttackComponent = MeleeAttackComponent(this, settings.attackDamage, 0.3f);
	override val attackDamage: Int by meleeAttackComponent::attackDamage;
	override val attackInterval: Float by meleeAttackComponent::attackInterval;
	private val moveComponent = MoveComponent(this, settings.speed);
	override val speed: Float by moveComponent::speed;
	private val autoTargeter = AutoTargeter(this) {
		if(world is SinglePlayerWorld)
			world.player
		else
			world.entities.getClosestOf<Player>(this)
	};
	override val target: LivingEntity? by autoTargeter::target;
	override val followRange: Float by autoTargeter::followRange;
	private val targetCenterFactor = 0.75f;
	private val approacher = ApproachTarget(this, targetCenterGapFactor = targetCenterFactor);
	private val meleeAttacker = MeleeAttackTarget(this, targetCenterFactor);
	private val attackingTexture = Textures.getShared("attacking_zombie");
	override val penetrationDamage = 1;
	override val damageInvincibilityDuration = 0.15f;
	private var attackTextureTimer = 0f;

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		moveComponent.move(delta, directionX, directionY);
	}

	override fun damageTarget(target: LivingEntity): Boolean = meleeAttackComponent.damageTarget(target);

	override fun meleeAttackNearby() {
		meleeAttackComponent.meleeAttackNearby();
	}

	override fun update(delta: Float) {
		super.update(delta);

		meleeAttackComponent.update(delta);

		updateAI(delta);
		if(approacher.state == ApproachTarget.State.APPROACHED && meleeAttacker.state == MeleeAttackTarget.State.ATTACKING) {
			attackTextureTimer -= delta;
			if(attackTextureTimer <= 0f)
				attackTextureTimer = 0.75f;
		} else {
			attackTextureTimer = 0f;
		}
	}

	protected open fun updateAI(delta: Float) {
		approacher.update(delta);
		if(approacher.state == ApproachTarget.State.APPROACHED)
			meleeAttacker.update(delta);
	}

	override fun draw(batch: SpriteBatch) {
		if(attackTextureTimer % 0.75f > 0.5f)
			super.draw(batch, attackingTexture, null);
		else
			super.draw(batch);
	}

	// 누군가(총잡이, 포탑, 좀비 등)가 자신을 공격하면 처치 대상을 그자로 한다. 단, 자연 생성된 포탑은 공격 불가이기 때문에 그것에게 공격받아도 그걸 타겟하지는 않는다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		val realAttacker = if(attacker is Landmine) attacker.installer else attacker;
		if(realAttacker is LivingEntity)
			autoTargeter.target = realAttacker;
	}

	/**
	 * 좀비 옵션
	 * 
	 * @property attackDamage 공격 피해량
	 * @property speed        이동 속도 (0이면 멈춤, 음수도 가능하지만 비권장)
	 * @throws IllegalArgumentException 값 일부가 잘못됐을 때
	 */
	class Properties(@JvmField val health: Int, @JvmField val attackDamage: Int = 0, @JvmField val speed: Float = 0f) {
		init {
			if(attackDamage < 0) throw IllegalArgumentException("invalid attack damage");
			if(health <= 0) throw IllegalArgumentException("invalid health");
		}
	}
}
