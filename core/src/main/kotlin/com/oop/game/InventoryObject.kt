package com.oop.game;

interface InventoryObject {
	val inventory: MutableList<Item>;
	
	fun addItemToInventory(item: Item) {
		inventory.add(item);
	}
	
	fun removeItemFromInventory(index: Int) {
		inventory.removeAt(index);
	}
	
	fun removeItemFromInventory(item: Item) {
		for(i in 0 until inventory.size)
			if(inventory[i] == item)
				inventory.removeAt(i);
	}
}
