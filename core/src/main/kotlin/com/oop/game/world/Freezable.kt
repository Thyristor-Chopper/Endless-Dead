package com.oop.game.world;

interface Freezable {
	val isFrozen: Boolean;

	fun freeze(duration: Float);
	
	fun unfreeze();
}
