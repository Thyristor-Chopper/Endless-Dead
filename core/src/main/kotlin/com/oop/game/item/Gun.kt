package com.oop.game.item;

import com.oop.game.Position;
import com.oop.game.entity.Bullet;
import com.oop.game.entity.Entity;
import com.oop.game.world.World;

abstract class Gun(
	world: World,
	id: String,
	name: String,
	damage: Int,
	speed: Float,
	val fireInterval : Float,
	private val maxAmmo : Int,
	var ammo : Int
) : Item(world, id, name), Shootable {
	override val bulletDamage = damage
	override val bulletSpeed = speed
	private var fireCooldown = 0f

	fun update(delta: Float) {
		if (fireCooldown > 0f) {
			fireCooldown -= delta
		}
	}
	
	fun canShoot(): Boolean {
		return fireCooldown <= 0f
	}
	
	fun startFireCooldown() {
		fireCooldown = fireInterval
	}
	
	override fun shoot(target: Position, shooter: Entity) {
		val bullet = Bullet(world, shooter.x, shooter.y, target, bulletSpeed, bulletDamage);
		world.add(bullet);
	}
}//maxAmmo와 ammo 프로퍼티도 필요할 듯? fireinterval, fire 함수도
