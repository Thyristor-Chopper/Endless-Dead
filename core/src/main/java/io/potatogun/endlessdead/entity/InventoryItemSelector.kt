package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.inventory.Inventory;
import io.potatogun.endlessdead.inventory.ObservableInventory;
import io.potatogun.endlessdead.item.Item;

/**
 * 인벤토리에서 아이템을 선택할 수 있는 개체에 대한 구현체.
 * 단독으로 사용하지 않고 위임으로만 사용된다.
 *
 * @property inventory 인벤토리
 */
class InventoryItemSelector(private val inventory: Inventory) : ItemSelectable {
	override val selectedItem: Item?
		get() {
			if(selectedItemIndex == -1) return null;
			try {
				return inventory.getItem(selectedItemIndex);
			} catch(e: IndexOutOfBoundsException) {
				val size = inventory.size;
				if(size == 0) {
					selectedItemIndex = -1;
					return null;
				} else {
					selectedItemIndex = size - 1;
					return inventory.getItem(selectedItemIndex);
				}
			}
		};
	override var selectedItemIndex: Int = -1
		private set(value) {
			if(value == -1) {
				field = -1;
			} else {
				val inventorySize = inventory.size;
				val newIndex = 
					if(value < 0) 0
					else if(value >= inventorySize) inventorySize - 1  // 비었다면 -1
					else value;
				field = newIndex;
			}
		};

	init {
		if(inventory is ObservableInventory) {
			inventory.addItemAddObserver {
				if(selectedItem == null)
					selectNextItem();
			};

			inventory.addItemRemoveObserver {
				val size = inventory.size;
				if(inventory.isEmpty)
					selectedItemIndex = -1;
				else if(selectedItemIndex >= size)
					selectedItemIndex = size - 1;
			};
		}
	}

	override fun selectNextItem(): Boolean {
		val index: Int = selectedItemIndex;
		val newIndex = 
			if(inventory.isEmpty)
				-1
			else if(index == -1 || index == inventory.size - 1)
				0
			else
				index + 1;
		selectedItemIndex = newIndex;
		return newIndex != -1;
	}

	override fun selectPreviousItem(): Boolean {
		val index: Int = selectedItemIndex;
		val newIndex = 
			if(inventory.isEmpty)
				-1
			else if(index == -1 || index == 0)
				inventory.size - 1
			else
				index - 1;
		selectedItemIndex = newIndex;
		return newIndex != -1;
	}

	override fun selectItem(item: Item): Boolean {
		val index = inventory.indexOf(item);
		if(index == -1) return false;
		selectedItemIndex = index;
		return true;
	}

	override fun selectItem(index: Int): Boolean {
		if(index < 0 || index >= inventory.size) return false;
		selectedItemIndex = index;
		return true;
	}

	override fun deselect() {
		selectedItemIndex = -1;
	}
}
