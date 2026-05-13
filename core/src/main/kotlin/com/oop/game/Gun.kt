package com.oop.game;

abstract class Gun(id: String, name: String, damage: Float, speed: Float) : Item(id, name), Shootable {
	override val bulletDamage = damage;
	override val bulletSpeed = speed;
	
	fun shoot() {
		
	}
}
