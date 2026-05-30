package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Position;
import io.potatogun.endlessdead.entity.Entity;
import io.potatogun.endlessdead.item.Fireable;
import io.potatogun.endlessdead.world.World;

import kotlin.math.sqrt;

/**
 * 총알 개체
 *
 * @param world			총알이 있는 세계
 * @param x 			처음 위치
 * @param y 			처음 위치
 * @param target		조준 위치
 * @param speed 		총알 속도
 * @param damage		총알이 주는 피해량
 * @param penetrable	총알 관통 가능 여부
 * @param hp			총알 체력(관통 시 감소)
 */
class Bullet(world: World, val gun: Fireable, val shooter: Entity, val target: Position, private val speed: Float, val damage: Int, val penetrable: Boolean, hp: Int) : LivingEntity(world, shooter.x, shooter.y, 16f, 16f, "bullet.bmp", hp) {
	override val canUpdateWhileFrozen = true;
	override val defaultInvincibleDuration = 0f;
	override val showDamagedIndicator = false;
	val amountX: Float;
	val amountY: Float;
	
	init {
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
		for(entity in world.getEntities())
			if(entity !== this && entity !== shooter && entity is LivingEntity && (!(entity is Bullet) || (entity is Bullet && entity.gun !== this.gun)) && collidesWith(entity)) {
				entity.takeDamage(damage, attacker=shooter);  // 무적 시간이 필요하면 추가...
				if(penetrable)
					this.takeDamage(entity.penetrationDamage, attacker=entity);
				else
					this.kill();
			}
	}
}
