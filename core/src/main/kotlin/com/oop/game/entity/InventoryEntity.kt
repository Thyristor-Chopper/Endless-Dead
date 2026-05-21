package com.oop.game.entity;

import com.oop.game.item.Item;

interface InventoryEntity {
	val inventory: MutableList<Item>;
	var selectedItemIndex: Int?;
	val holdingItem: Item?
		get() {
			val index: Int? = selectedItemIndex;
			if(index == null) return null;
			return inventory[index];
		};
	
	fun addItemToInventory(item: Item) {
		inventory.add(item);
	}
	
	fun removeItemFromInventory(index: Int) {
		val currentIndex: Int? = selectedItemIndex;
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == currentIndex) {
			if(currentIndex == 0) selectedItemIndex = 1;
			else selectedItemIndex = (selectedItemIndex ?: 1) - 1;
		}
	}
	
	fun removeItemFromInventory(item: Item) {
		for(i in 0 until inventory.size)
			if(inventory[i] == item)
				inventory.removeAt(i);
		if(inventory.isEmpty())
			selectedItemIndex = null;
	}
	
	fun selectNextItem() {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index >= inventory.size - 1)
			selectedItemIndex = 0;
		else
			selectedItemIndex = (selectedItemIndex ?: 0) + 1;
	}
	
	fun selectPreviousItem() {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index <= 0)
			selectedItemIndex = inventory.size - 1;
		else
			selectedItemIndex = (selectedItemIndex ?: 1) - 1;
	}
}
