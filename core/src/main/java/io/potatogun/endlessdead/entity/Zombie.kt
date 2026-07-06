package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.ai.ApproachTarget;
import io.potatogun.endlessdead.entity.ai.DashToTarget;
import io.potatogun.endlessdead.entity.ai.MeleeAttackTarget;
import io.potatogun.endlessdead.entity.component.AutoTargeter;
import io.potatogun.endlessdead.entity.component.MeleeAttackComponent;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.util.Utils;
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
sealed class Zombie(world: World, name: String, x: Float, y: Float, width: Float, height: Float, settings: Properties) : LivingEntity(world, name, x, y, width, height, settings.health, Textures.getShared("zombie")), MeleeAttackable, DamageListener, PenetratorDamagable, Movable, Targetable {
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
	private val targetCenterFactor = 3f / 4f;
	private val approacher = ApproachTarget(this, targetCenterGapFactor = targetCenterFactor);
	private val meleeAttacker = MeleeAttackTarget(this, targetCenterFactor);
	private val attackingTexture = Textures.getShared("attacking_zombie");
	override val penetrationDamage = 1;
	override val damageInvincibilityDuration = 0.15f;
	private var attackCooldownTimer = attackInterval;
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
		if(meleeAttacker.state == MeleeAttackTarget.State.ATTACKING) {
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

	// 누군가가 자신을 공격하면 처치 대상을 그자로 한다.
	//   자연 생성된 포탑은 공격 불가이기 때문에 그것에게 공격받아도 그걸 타겟하지는 않는다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			autoTargeter.target = attacker;
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

	/**
	 * 약한 좀비
	 *
	 * @param world 개체가 속한 세계
	 * @param x     개체의 처음 X 위치
	 * @param y     개체의 처음 Y 위치
	 */
	class Weak(world: World, x: Float, y: Float) : Zombie(world, "Small Zombie", x, y, 21f, 30f, Zombie.Properties(3, 1, 150f));

	/**
	 * 보통 좀비
	 *
	 * @param world 개체가 속한 세계
	 * @param x     개체의 처음 X 위치
	 * @param y     개체의 처음 Y 위치
	 */
	class Normal(world: World, x: Float, y: Float) : Zombie(world, "Zombie", x, y, 32f, 45f, Zombie.Properties(6, 3, 100f));

	/**
	 * 강한 좀비
	 *
	 * @param world 개체가 속한 세계
	 * @param x     개체의 처음 X 위치
	 * @param y     개체의 처음 Y 위치
	 */
	class Strong(world: World, x: Float, y: Float) : Zombie(world, "Rabid Zombie", x, y, 49f, 70f, Zombie.Properties(15, 5, 50f)) {
		private val dasher = DashToTarget(this, 20, 800f, 250f);
		// 강한 좀비는 살짝 붉게
		override val overlayColor = Utils.rgb(255, 204, 204);

		override fun updateAI(delta: Float) {
			dasher.update(delta);
			if(dasher.state == DashToTarget.State.STANDBY || dasher.state == DashToTarget.State.COOLDOWN)
				super.updateAI(delta);
		}
	}
}
