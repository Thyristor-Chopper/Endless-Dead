package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.ai.ApproachTarget;
import io.potatogun.endlessdead.entity.ai.MeleeAttackTarget;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.getClosestOf;
import io.potatogun.gdxhelper.world.World;

/**
 * 좀비 — 플레이어를 따라 자동으로 이동하고 손공격하는 몹
 *
 * @param world    개체가 속한 세계
 * @param Zombie   개체 이름
 * @param x        개체의 처음 X 위치
 * @param y        개체의 처음 Y 위치
 * @param width    가로 크기 (픽셀)
 * @param height   세로 크기 (픽셀)
 * @param settings 좀비 옵션
 */
abstract class Zombie(world: World, name: String, x: Float, y: Float, width: Float, height: Float, settings: Properties) : Mob(world, name, x, y, width, height, settings.health, Textures.getShared("zombie")), DamageListener {
	private val meleeAttacker = MeleeAttackTarget(this, 3f / 4f);
	private val attackingTexture = Textures.getShared("attacking_zombie");
	override val attackDamage = settings.attackDamage;
	override val attackInterval = 0.3f;
	override val movementSpeed = settings.speed;
	override val penetrationDamage = 1;
	override val damageInvincibilityDuration = 0.15f;
	private var attackCooldownTimer = attackInterval;
	private var attackTextureTimer = 0f;

	override fun findNewTarget(): LivingEntity? {
		val world = this.world;
		if(world is SinglePlayerWorld)
			return world.player;
		else
			return world.entities.getClosestOf<Player>(this);
	}

	final override fun update(delta: Float) {
		super.update(delta);

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
		meleeAttacker.update(delta);
	}

	override fun draw(batch: SpriteBatch) {
		if(attackTextureTimer % 0.75f > 0.5f)
			super.draw(batch, attackingTexture);
		else
			super.draw(batch);
	}

	// 누군가가 자신을 공격하면 처치 대상을 그자로 한다.
	//   자연 생성된 포탑은 공격 불가이기 때문에 그것에게 공격받아도 그걸 타겟하지는 않는다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			target = attacker;
	}

	/**
	 * 좀비 옵션
	 * 
	 * @property attackDamage 공격 피해량
	 * @property speed        이동 속도 (0이면 멈춤, 음수도 가능하지만 비권장)
	 * @throws IllegalArgumentException 값 일부가 잘못됐을 때
	 */
	open class Properties(@JvmField val health: Int, @JvmField val attackDamage: Int = 0, @JvmField val speed: Float = 0f) {
		init {
			if(attackDamage < 0) throw IllegalArgumentException("invalid attack damage");
			if(health <= 0) throw IllegalArgumentException("invalid health");
		}
	}
}
