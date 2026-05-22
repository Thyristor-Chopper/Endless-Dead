package com.oop.game.item;

import com.oop.game.Position;
import com.oop.game.entity.Entity;

interface Fireable {
	val bulletDamage: Int;
	val bulletSpeed: Float;
	
	fun fire(target: Position, shooter: Entity);
}
