package com.oop.game;

interface InventoryObject {
	val inventory: MutableList<Item>;
	var selectedItemIndex: Int?;
	val holdingItem: Item?
		get() {
			if(selectedItemIndex == null) return null;
			return inventory[selectedItemIndex];
		};
	
	fun addItemToInventory(item: Item) {
		inventory.add(item);
	}
	
	fun removeItemFromInventory(index: Int) {
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == selectedItemIndex) {
			if(selectedItemIndex == 0) selectedItemIndex = 1;
			else selectedItemIndex--;
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
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(selectedItemIndex == null)
			selectedItemIndex = 0;
		else if(selectedItemIndex >= inventory.size - 1)
			selectedItemIndex = 0;
		else
			selectedItemIndex++;
	}
	
	fun selectPreviousItem() {
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(selectedItemIndex == null)
			selectedItemIndex = 0;
		else if(selectedItemIndex <= 0)
			selectedItemIndex = inventory.size - 1;
		else
			selectedItemIndex--;
	}
}
