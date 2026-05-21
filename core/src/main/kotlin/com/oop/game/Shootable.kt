package com.oop.game;

interface Shootable {
	val bulletDamage: Int;
	val bulletSpeed: Float;
	
	fun shoot(target: Position, shooter: LivingGameObject);
}
