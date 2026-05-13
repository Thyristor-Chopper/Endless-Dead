package com.oop.game;

interface Shootable {
	val bulletDamage: Float;
	val bulletSpeed: Float;
	
	fun shoot(direction: Float);
}
