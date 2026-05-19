package com.oop.game;

abstract class Gun(world: GameWorld, id: String, name: String, damage: Int, speed: Float) : Item(world, id, name), Shootable {
	override val bulletDamage = damage;
	override val bulletSpeed = speed;
	
	override fun shoot(target: Position, shooter: LivingGameObject) {
		val bullet = Bullet(world, shooter.x, shooter.y, target, bulletSpeed, bulletDamage);
		world.add(bullet);
	}
}
