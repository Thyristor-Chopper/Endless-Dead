package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.util.Math.max2;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.sqrt;

/**
 * 총알 개체
 *
 * @param    world        총알이 있는 세계
 * @property gun          쏜 발사기
 * @property shooter      쏜 개체
 * @property target       총알이 향할 위치
 * @property speed        총알 속도
 * @property damage       총알이 주는 피해량
 * @property isPenetrable 총알 관통 가능 여부
 * @param    health       총알 관통력
 * @param    size         총알 지름
 * @param    texture      총알 텍스처
 */
class Bullet @JvmOverloads constructor(world: World, val gun: Shootable, val shooter: Entity, private val target: Position, speed: Float, val damage: Int, val isPenetrable: Boolean, health: Int, size: Float = 16f, texture: Texture = Textures.getShared("bullet")) : LivingEntity(world, "Bullet", shooter.position.x, shooter.position.y, size, size, health, texture), Movable {
	private val moveComponent = MoveComponent(this, speed);
	override val speed: Float by moveComponent::speed;
	override val isUpdatableWhileFrozen = (shooter is Player);
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

	init {
		if(speed < 0f) throw IllegalArgumentException("invalid speed");
		if(damage < 0) throw IllegalArgumentException("invalid damage");
		if(health < 0f) throw IllegalArgumentException("invalid health");

		val dx = target.x - shooter.x;
		val dy = target.y - shooter.y;
		val distance = sqrt(dx * dx + dy * dy);
		if(distance > 0f) {
			directionX = dx / distance;
			directionY = dy / distance;
		} else {
			directionX = 0f;
			directionY = 0f;
			this.remove();  // 안 움직이는 총알 방지
		}

		if(shooter is TeamMember)
			team = shooter.team;
	}

	override fun update(delta: Float) {
		super.update(delta);

		move(delta, directionX, directionY);

		// 화면 밖으로 나가면 소멸
		val maxHalfLength = max2(width, height) * 0.5f;
		if(x < 0f - maxHalfLength || x > world.width + maxHalfLength || y < 0f - maxHalfLength || y > world.height + maxHalfLength)
			this.remove();

		// 날아갈 때마다 임의의 개체랑 충돌하는지 검사해서 대미지 주고 총알은 소멸.
		forEachNearby { entity ->
			if(entity !== this && entity !== shooter && entity is LivingEntity && !entity.isInvincible && !isSameTeamWith(entity) && (entity !is Bullet || entity.gun !== this.gun) && collidesWith(entity)) {
				entity.takeDamage(damage, attacker = shooter);  // 무적 시간이 필요하면 추가...
				if(isPenetrable) {
					if(entity is PenetratorDamagable)
						this.takeDamage(entity.penetrationDamage, attacker = entity);
				} else {
					this.remove();
				}
			}
		};
	}

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		moveComponent.move(delta, directionX, directionY);
	}
}
