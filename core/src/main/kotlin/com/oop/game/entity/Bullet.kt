package com.oop.game.entity;

import com.oop.game.Position;
import com.oop.game.entity.Entity;
import com.oop.game.world.World;

import kotlin.math.sqrt;

/**
 * 총알 개체
 *
 * @param world		총알이 있는 세계
 * @param x 		처음 위치
 * @param y 		처음 위치
 * @param target	조준 위치
 * @param speed 	총알 속도
 * @param damage	총알이 주는 피해량
 */
class Bullet(world: World, x: Float, y: Float, val target: Position, private val speed: Float, val damage: Int) : Entity(world, x, y, 16.0f, 16.0f, "bullet.png") {
    var isAlive = true
		private set;
    private val dx = target.x - x;
    private val dy = target.y - y;
    private val distance = sqrt(dx * dx + dy * dy);
	
	override fun update(delta: Float) {
		if (distance > 0f) {
			x += dx / distance * speed * delta
			y += dy / distance * speed * delta
		}
		
		// 화면 밖으로 나가면 소멸
		if(x < 0f || x > world.width || y < 0f || y > world.height)
			isAlive = false
	}

	/*
	fun bulletTarget(
		bullet: Bullet,
		target: Position
	): Float {
		val dx = target.x - bullet.x
		val dy = target.y - bullet.y
		return sqrt(dx * dx + dy * dy)
	}
	*/
}
