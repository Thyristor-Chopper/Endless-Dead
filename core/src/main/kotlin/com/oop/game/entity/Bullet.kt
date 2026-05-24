package com.oop.game.entity;

import com.oop.game.Position;
import com.oop.game.ScoreManager;
import com.oop.game.entity.Entity;
import com.oop.game.world.World;

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
 */
class Bullet(world: World, val shooter: Entity, val target: Position, private val speed: Float, val damage: Int, val penetrable: Boolean) : Entity(world, shooter.x, shooter.y, 16.0f, 16.0f, "bullet.bmp") {
    var isAlive = true
		private set;
    private val dx = target.x - shooter.x;
    private val dy = target.y - shooter.y;
    private val distance = sqrt(dx * dx + dy * dy);
	
	override fun update(delta: Float) {
		if (distance > 0f) {
			x += dx / distance * speed * delta
			y += dy / distance * speed * delta
		}
		
		// 화면 밖으로 나가면 소멸
		if(x < 0f || x > world.width || y < 0f || y > world.height)
			isAlive = false
		
		// 날아갈 때마다 임의의 개체랑 충돌하는지 검사해서 대미지 주고 총알은 소멸.
		for(entity in world.getEntities())
			if(entity !== this && entity !== world.player && !(entity is Bullet) && entity is LivingEntity && collidesWith(entity)) {
				entity.takeDamage(damage, 1.0f, shooter);  // 무적 시간이 필요하면 추가...
				if(shooter === world.player)
					ScoreManager.addScore(10);
				if(!penetrable) isAlive = false;
			}
	}
}
