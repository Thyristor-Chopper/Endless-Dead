package com.oop.game;

abstract class Item(val world: GameWorld, val id: String, val name: String) {
	fun equals(other: Item): Boolean {
		return id == other.id;
	}
}
