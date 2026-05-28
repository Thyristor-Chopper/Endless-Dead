package com.oop.game.entity;

import com.oop.game.item.Item;

class BasicInventoryEntity : InventoryEntity {
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
	
	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item	추가할 아이템
	 */
	override fun addItemToInventory(item: Item, select: Boolean) {
		inventory.add(item);
		if(select) selectedItemIndex = inventory.size - 1;
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param index	아이템 위치
	 */
	override fun removeItemFromInventory(index: Int) {
		val currentIndex: Int? = selectedItemIndex;
		inventory[index].holder = null;
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == currentIndex)
			selectPreviousItem();
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param 	item	제거할 아이템
	 * @return 	성공 여부
	 */
	override fun removeItemFromInventory(item: Item): Boolean {
		var found = false;
		if(!inventory.isEmpty())
			for(i in 0 until inventory.size)
				if(inventory[i] === item) {
					found = true;
					inventory[i].holder = null;
					inventory.removeAt(i);
					if(i == selectedItemIndex)
						selectPreviousItem();
					break;
				}
		return found;
	}
	
	/**
	 * 인벤토리의 다음 아이템 선택
	 */
	override fun selectNextItem() {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index == inventory.size - 1)
			selectedItemIndex = 0;
		else
			selectedItemIndex = index + 1;
	}
	
	/**
	 * 인벤토리의 이전 아이템 선택
	 */
	override fun selectPreviousItem() {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index == 0)
			selectedItemIndex = inventory.size - 1;
		else
			selectedItemIndex = index - 1;
	}
	
	/**
	 * 지정한 아이템을 갖고 있다면 선택한다.
	 *
	 * @return 성공 여부
	 */
	override fun selectItem(item: Item): Boolean {
		val index = inventory.indexOfFirst({ it === item });
		if(index == -1) return false;
		selectedItemIndex = index;
		return true;
	}
	
	/**
	 * 지정한 인덱스의 아이템을 선택한다.
	 */
	override fun selectItem(index: Int) {
		if(index < 0 || index >= inventory.size) throw IllegalArgumentException("index out of bounds");
		selectedItemIndex = index;
	}
	
	/**
	 * 지정한 아이템이 있는지 확인
	 */
	override fun hasItem(item: Item): Boolean = item in inventory;
	
	/**
	 * 인벤토리의 읽기용 사본을 가져온다.
	 */
	override fun getInventory(): List<Item> = inventory.toList();
}
