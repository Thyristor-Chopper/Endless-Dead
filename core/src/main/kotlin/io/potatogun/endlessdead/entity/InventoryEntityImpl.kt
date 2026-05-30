package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Item;

/**
 * 인벤토리를 가진 개체의 기본적인 구현
 * 단독으로 사용하지 않고 위임으로만 사용된다.
 */
class InventoryEntityImpl : InventoryEntity {
	private val inventory = mutableListOf<Item>();
	override val selectedItem: Item?
		get() = selectedItemIndex?.let { inventory[it] };
	override var selectedItemIndex: Int? = null
		private set(value) {
			if(value == null) {
				field = null;
			} else {
				val inventorySize = inventory.size;
				if(value < 0) field = 0;
				else if(value >= inventorySize) field = inventorySize - 1;
				else field = value;
			}
		};
	override val inventoryItemCount: Int
		get() = inventory.size;
	override val isInventoryEmpty: Boolean
		get() = inventory.isEmpty();
	
	override fun addItemToInventory(item: Item, select: Boolean): Boolean {
		if(hasItem(item)) return false;
		item.holder?.removeItemFromInventory(item);
		item.container?.removeItem();
		inventory.add(item);
		if(select) selectedItemIndex = inventory.size - 1;
		return true;
	}
	
	override fun removeItemFromInventory(index: Int) {
		if(index < 0 || index >= inventory.size) throw IllegalArgumentException("index out of bounds");
		val currentIndex: Int? = selectedItemIndex;
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == currentIndex)
			selectPreviousItem();
	}
	
	override fun removeItemFromInventory(item: Item): Boolean {
		var found = false;
		if(!inventory.isEmpty())
			for(i in 0 until inventory.size)
				if(inventory[i] === item) {
					found = true;
					inventory.removeAt(i);
					if(i == selectedItemIndex)
						selectPreviousItem();
					break;
				}
		return found;
	}
	
	override fun selectNextItem(): Boolean {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index == inventory.size - 1)
			selectedItemIndex = 0;
		else
			selectedItemIndex = index + 1;
		return selectedItemIndex != null;
	}
	
	override fun selectPreviousItem(): Boolean {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index == 0)
			selectedItemIndex = inventory.size - 1;
		else
			selectedItemIndex = index - 1;
		return selectedItemIndex != null;
	}
	
	override fun selectItem(item: Item): Boolean {
		val index = inventory.indexOfFirst({ it === item });
		if(index == -1) return false;
		selectedItemIndex = index;
		return true;
	}
	
	override fun selectItem(index: Int) {
		if(index < 0 || index >= inventory.size) throw IllegalArgumentException("index out of bounds");
		selectedItemIndex = index;
	}
	
	override fun hasItem(item: Item): Boolean = inventory.any { it === item };
	
	override fun getInventory(): List<Item> = inventory.toList();
}
