package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.util.max2;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.sqrt;

/**
 * 총알
 *
 * @param    world        총알이 있는 세계
 * @property shooter      쏜 개체
 * @property target       총알이 향할 위치
 * @property speed        총알 속도
 * @property damage       총알이 주는 피해량
 * @property isPenetrable 총알 관통 가능 여부
 * @param    health       총알 관통력
 * @param    size         총알 지름
 * @param    texture      총알 텍스처
 */
open class Bullet @JvmOverloads constructor(world: World, val shooter: Entity, target: Position?, speed: Float, val damage: Int, val isPenetrable: Boolean, health: Int, size: Float = 16f, texture: Texture = EndlessDead.instance.resources.textures.getShared("bullet"), initialPosition: Position? = null) : LivingEntity(world, "Bullet", if(initialPosition != null) initialPosition.x else shooter.x, if(initialPosition != null) initialPosition.y else shooter.y, size, size, health, texture), Movable {
	private val game = EndlessDead.instance;
	private val moveComponent = MoveComponent(this, speed);
	override val speed: Float by moveComponent::speed;
	override val isUpdatableWhileFrozen = (shooter === game.player);
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("canShowDamageIndicator")
	override val showDamageIndicator = false;
	override val damageInvincibilityDuration = 0.1f;
	/**
	 * 총알 속도에 따른 가로 이동량
	 */
	private val directionX: Float;
	/**
	 * 총알 속도에 따른 세로 이동량
	 */
	private val directionY: Float;
	private val slack = 128f;  // 경계 밖으로 이만큼은 나가도 살아있게

	init {
		if(damage < 0) throw IllegalArgumentException("invalid damage");
		if(target != null && speed == 0f) throw IllegalArgumentException("bullet with zero speed can't have a target");

		if(target != null) {
			val dx = target.x - x;
			val dy = target.y - y;
			val distance = sqrt(dx * dx + dy * dy);
			if(distance != 0f) {
				directionX = dx / distance;
				directionY = dy / distance;
			} else {
				directionX = 0f;
				directionY = 0f;
				game.taskQueue.post(this) { bullet -> (bullet as Bullet).remove() };  // 이동해야 하는(속도가 있는) 총알이 이동하지 못하면 소멸
			}
		} else {
			directionX = 0f;
			directionY = 0f;
		}

		if(shooter is TeamMember)
			team = shooter.team;
	}

	override fun update(delta: Float) {
		super.update(delta);

		move(delta, directionX, directionY);

		// 월드 밖으로 나가면 소멸
		val world = level;
		val maxHalfLength = max2(width, height) * 0.5f;
		if(world != null && (x < 0f - maxHalfLength - slack || x > world.width + maxHalfLength + slack || y < 0f - maxHalfLength - slack || y > world.height + maxHalfLength + slack))
			this.remove();

		// 날아갈 때마다 임의의 개체랑 충돌하는지 검사해서 대미지를 준다.
		forEachNearby { entity ->
			if(entity !== shooter && entity is LivingEntity && !entity.isInvincible && !isSameTeamWith(entity) && collidesWith(entity)) {
				if(!onCollidePreProcess(entity)) return@forEachNearby;
				entity.takeDamage(damage, attacker = shooter);  // 무적 시간이 필요하면 추가...
				val next = onCollideProcessed(entity);
				if(isPenetrable) {
					if(entity is PenetratorDamagable) {
						this.takeDamage(entity.penetrationDamage, attacker = entity);
						if(!isAlive)
							return;
					}
					if(!next)
						return;
				} else {
					this.remove();
					return;
				}
			}
		};
	}

	/**
	 * 총알이 살아있는 개체와 충돌했을 때 호출되는 콜백이다. (정확히는 충돌 처리 직전에 호출된다.)
	 *
	 * @return 이번 개체를 처리할지의 여부
	 */
	protected open fun onCollidePreProcess(entity: LivingEntity): Boolean = true;

	/**
	 * 총알이 살아있는 개체와 충돌돼서 처리된 이후 호출되는 콜백이다.
	 *
	 * @return 충돌 처리를 계속할지의 여부 (비관통 총알은 효과 없음)
	 */
	protected open fun onCollideProcessed(entity: LivingEntity): Boolean = true;

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		moveComponent.move(delta, directionX, directionY);
	}
}
