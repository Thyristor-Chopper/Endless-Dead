package com.oop.game.entity;

import com.oop.game.ItemHolder;
import com.oop.game.item.Item;

/**
 * 인벤토리를 가지는 개체에 대한 인터페이스
 */
interface InventoryEntity : ItemHolder {
	val inventory: MutableList<Item>;
	var selectedItemIndex: Int?;
	val selectedItem: Item?
		get() {
			val index: Int? = selectedItemIndex;
			if(index == null) return null;
			return inventory[index];
		};
	
	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item	추가할 아이템
	 */
	fun addItemToInventory(item: Item, select: Boolean = false) {
		inventory.add(item);
		if(selectedItemIndex == null)
			selectedItemIndex = 0;
		else if(select)
			selectedItemIndex = inventory.size - 1;
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param index	아이템 위치
	 */
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
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param 	item	제거할 아이템
	 * @return 	성공 여부
	 */
	fun removeItemFromInventory(item: Item): Boolean {
		var found = false;
		if(inventory.size > 0)
			for(i in 0 until inventory.size)
				if(inventory[i] === item) {
					found = true;
					inventory.removeAt(i);
					if(i == selectedItemIndex)
						selectPreviousItem();
					break;
				}
		if(inventory.isEmpty())
			selectedItemIndex = null;
		return found;
	}
	
	/**
	 * 인벤토리의 다음 아이템 선택
	 */
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
	
	/**
	 * 인벤토리의 이전 아이템 선택
	 */
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
	
	override fun getHoldingItem(): Item? = selectedItem;
	
	override fun setHoldingItem(item: Item) {
		if(inventory.size > 0)
			for(i in 0 until inventory.size)
				if(inventory[i] === item) {
					selectedItemIndex = i;
					return;
				}
		
		// 인벤토리에 없을 때
		addItemToInventory(item, true);
	}
	
	override fun destroyHoldingItem(): Boolean {
		val index: Int? = selectedItemIndex;
		if(index == null) return false;
		removeItemFromInventory(index);
		return true;
	}
	
	override fun destroyItem(item: Item): Boolean {
		return removeItemFromInventory(item);
	}
}
