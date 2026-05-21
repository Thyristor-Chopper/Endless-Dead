package com.oop.game.item;

import com.oop.game.Position;
import com.oop.game.entity.Entity;

interface Shootable {
	val bulletDamage: Int;
	val bulletSpeed: Float;
	
	fun shoot(target: Position, shooter: Entity);
}
