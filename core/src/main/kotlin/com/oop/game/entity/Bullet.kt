package com.oop.game.entity;

import com.oop.game.Position;
import com.oop.game.entity.Entity;
import com.oop.game.world.World;

import kotlin.math.sqrt;

class Bullet(
	world: World,
    x: Float,
    y: Float,
    val target: Position,
    private val speed: Float,
    val damage: Int
) : Entity(world, x, y, 16.0f, 16.0f, "bullet.png") {
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
		if (x < 0f || x > world.width || y < 0f || y > world.height) {
			isAlive = false
		}
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
