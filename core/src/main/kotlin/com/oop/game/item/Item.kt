package com.oop.game.item;

import com.oop.game.world.World;

abstract class Item(val world: World, val id: String, val name: String) {
	fun equals(other: Item): Boolean {
		return id == other.id;
	}
}
