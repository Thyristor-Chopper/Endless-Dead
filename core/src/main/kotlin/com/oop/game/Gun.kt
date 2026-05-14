package com.oop.game;

abstract class Gun(world: GameWorld, id: String, name: String, damage: Float, speed: Float) : Item(world, id, name), Shootable {
	override val bulletDamage = damage;
	override val bulletSpeed = speed;
	
	override fun shoot() {
		// val bullet = Bullet(world, )
	}
}
