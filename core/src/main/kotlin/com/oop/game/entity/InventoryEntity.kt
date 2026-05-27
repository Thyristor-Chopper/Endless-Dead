package com.oop.game.entity;

import com.oop.game.item.Item;

import java.util.WeakHashMap;

// 원래는 객체의 private 필드로써 기능하는 변수들
// 전역 private 맵으로 한 이유:
//   이것은 내부 데이타로 원래 외부에서 접근하면 안 되지만
//   interface는 private/protected 필드를 지원하지 않기 때문.
// 또한 WeakHashMap를 쓴 이유는 removeDead에 의해 클래스가 더 이상 사용되지 않더라도
//   일반 맵을 쓰면 해당 객체를 키로 아직 갖고 있어서 계속 메모리에 남아있고 gc가 안 돼서 그렇다.
private val inventories = WeakHashMap<InventoryEntity, MutableList<Item>>();
private val selectedItemIndexes = WeakHashMap<InventoryEntity, Int?>();

/**
 * 인벤토리를 가지는 개체에 대한 인터페이스
 */
interface InventoryEntity {
	val selectedItemIndex: Int?
		get() = selectedItemIndexes[this];
	val selectedItem: Item?
		get() {
			val inventory = inventoryOf(this);
			val index: Int? = selectedItemIndex;
			if(index == null) return null;
			return inventory[index];
		};
	val inventoryItemCount: Int
		get() = inventoryOf(this).size;
	
	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item	추가할 아이템
	 */
	fun addItemToInventory(item: Item, select: Boolean = false) {
		val inventory = inventoryOf(this);
		inventory.add(item);
		if(selectedItemIndexes[this] == null)
			selectedItemIndexes[this] = 0;
		else if(select)
			selectedItemIndexes[this] = inventory.size - 1;
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param index	아이템 위치
	 */
	fun removeItemFromInventory(index: Int) {
		val inventory = inventoryOf(this);
		val currentIndex: Int? = selectedItemIndexes[this];
		inventory[index].holder = null;
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndexes[this] = null;
		else if(index == currentIndex) {
			if(currentIndex == 0) selectedItemIndexes[this] = 1;
			else selectedItemIndexes[this] = (selectedItemIndexes[this] ?: 1) - 1;
		}
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param 	item	제거할 아이템
	 * @return 	성공 여부
	 */
	fun removeItemFromInventory(item: Item): Boolean {
		val inventory = inventoryOf(this);
		var found = false;
		if(inventory.size > 0)
			for(i in 0 until inventory.size)
				if(inventory[i] === item) {
					found = true;
					inventory[i].holder = null;
					inventory.removeAt(i);
					if(i == selectedItemIndexes[this])
						selectPreviousItem();
					break;
				}
		if(inventory.isEmpty())
			selectedItemIndexes[this] = null;
		return found;
	}
	
	/**
	 * 인벤토리의 다음 아이템 선택
	 */
	fun selectNextItem() {
		val inventory = inventoryOf(this);
		val index: Int? = selectedItemIndexes[this];
		if(inventory.isEmpty())
			selectedItemIndexes[this] = null;
		else if(index == null)
			selectedItemIndexes[this] = 0;
		else if(index >= inventory.size - 1)
			selectedItemIndexes[this] = 0;
		else
			selectedItemIndexes[this] = (selectedItemIndexes[this] ?: 0) + 1;
	}
	
	/**
	 * 인벤토리의 이전 아이템 선택
	 */
	fun selectPreviousItem() {
		val inventory = inventoryOf(this);
		val index: Int? = selectedItemIndexes[this];
		if(inventory.isEmpty())
			selectedItemIndexes[this] = null;
		else if(index == null)
			selectedItemIndexes[this] = 0;
		else if(index <= 0)
			selectedItemIndexes[this] = inventory.size - 1;
		else
			selectedItemIndexes[this] = (selectedItemIndexes[this] ?: 1) - 1;
	}
	
	/**
	 * 지정한 아이템을 갖고 있다면 선택한다.
	 *
	 * @return 성공 여부
	 */
	fun selectItem(item: Item): Boolean {
		val index = inventoryOf(this).indexOfFirst({ it === item });
		if(index == -1) return false;
		selectedItemIndexes[this] = index;
		return true;
	}
	
	/**
	 * 지정한 인덱스의 아이템을 선택한다.
	 */
	fun selectItem(index: Int) {
		if(index < 0 || index >= inventoryOf(this).size) throw IllegalArgumentException("index out of bounds");
		selectedItemIndexes[this] = index;
	}
	
	/**
	 * 지정한 아이템이 있는지 확인
	 */
	fun hasItem(item: Item): Boolean = item in inventoryOf(this);
	
	/**
	 * 인벤토리의 읽기용 사본을 가져온다.
	 */
	fun getInventory(): List<Item> = inventoryOf(this).toList();
}

private inline fun inventoryOf(entity: InventoryEntity) = inventories.getOrPut(entity, { mutableListOf<Item>() });
