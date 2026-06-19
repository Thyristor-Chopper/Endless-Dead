package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.item.Fireable;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.sqrt;

/**
 * 총알 개체
 *
 * @param world			총알이 있는 세계
 * @param gun 			쏜 총
 * @param shooter		쏜 개체
 * @param target		총알이 향할 위치
 * @param speed 		총알 속도
 * @param damage		총알이 주는 피해량
 * @param penetrable	총알 관통 가능 여부
 * @param hp			총알 체력 (관통 시 감소)
 */
class Bullet(world: World, val gun: Fireable, val shooter: Entity, private val target: Position, private val speed: Float, private val damage: Int, private val penetrable: Boolean, hp: Int) : LivingEntity(world, shooter.position.x, shooter.position.y, 16f, 16f, Textures.getShared("bullet"), hp) {
	override val isUpdatableWhileFrozen = (shooter is Player);
	override val defaultInvincibleDuration = 0f;
	override val showDamagedIndicator = false;
	private val amountX: Float;
	private val amountY: Float;

	init {
		if(speed < 0f) throw IllegalArgumentException("invalid speed");
		if(damage < 0) throw IllegalArgumentException("invalid damage");
		if(hp < 0f) throw IllegalArgumentException("invalid HP");

		val dx = target.x - shooter.x;
		val dy = target.y - shooter.y;
		val distance = sqrt(dx * dx + dy * dy);

		if(distance > 0f) {
			amountX = dx / distance * speed;
			amountY = dy / distance * speed;
		} else {
			this.kill();
			amountX = 0f;
			amountY = 0f;
		}
	}

	override fun update(delta: Float) {
		x += amountX * delta;
		y += amountY * delta;

		// 화면 밖으로 나가면 소멸
		if(x < 0f || x > world.width || y < 0f || y > world.height)
			this.kill();

		// 날아갈 때마다 임의의 개체랑 충돌하는지 검사해서 대미지 주고 총알은 소멸.
		// 처음 템플릿(예제) 코드에서는 모든 상호작용을 월드에서 처리했으나
		//   난 총알'이' 누군가에게 직접 대미지를 주는 게 맞는 것 같아서 여기서 처리함.
		for(entity in world.getEntities())
			if(entity !== this && entity !== shooter && entity is LivingEntity && (entity !is Bullet || entity.gun !== this.gun) && collidesWith(entity)) {
				entity.takeDamage(damage, attacker=shooter);  // 무적 시간이 필요하면 추가...
				if(penetrable) {
					val penetrationDamage = if(entity is PenetratorDamagable) entity.penetrationDamage else 0;
					this.takeDamage(penetrationDamage, attacker = entity);
				} else {
					this.kill();
				}
			}
	}

	/**
	 * 공유 자원이기 때문에 여기서 정리하지 않고 다른 인스턴스에서 재활용한다.
	 */
	override fun dispose() {}
}
