package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 하나의 아이템만 가질 수 있는 인벤토리 구현체.
 * 단독으로 사용하지 않고 위임으로 사용된다.
 */
class SingleItemInventory : InventoryEntity {
	private var inventoryItem: Item? = null;
	override var selectedItem: Item? = null;
	override var selectedItemIndex: Int? = null;
	override val itemCount: Int
		get() = if(inventoryItem != null) 1 else 0;
	override val isInventoryEmpty: Boolean
		get() = (inventoryItem == null);
	override val maxSlots = 1;
	override val firstItem: Item?
		get() = inventoryItem;
	override val lastItem: Item?
		get() = inventoryItem;

	override fun addItem(item: Item, select: Boolean): Boolean {
		if(inventoryItem != null) return false;
		val holder: Entity? = item.holder;
		if(holder is InventoryEntity)
			holder.removeItem(item);
		inventoryItem = item;
		if(select) {
			selectedItem = item;
			selectedItemIndex = 0;
		}
		return true;
	}

	override fun removeItem(index: Int) {
		if(index != 0 || inventoryItem == null)
			throw IllegalArgumentException("index out of bounds");
		inventoryItem = null;
		selectedItem = null;
	}

	override fun removeItem(item: Item): Boolean {
		if(inventoryItem !== item) return false;
		inventoryItem = null;
		selectedItem = null;
		return true;
	}

	override fun getItem(index: Int): Item {
		val item: Item? = inventoryItem;
		if(index != 0 || item == null) throw IllegalArgumentException("index out of bounds");
		return item;
	}

	override fun selectNextItem(): Boolean {
		return inventoryItem?.let { selectedItem = it; true } ?: false;
	}

	override fun selectPreviousItem(): Boolean {
		return inventoryItem?.let { selectedItem = it; true } ?: false;
	}

	override fun selectItem(item: Item): Boolean {
		if(item === inventoryItem) {
			selectedItem = inventoryItem;
			return true;
		}
		return false;
	}

	override fun selectItem(index: Int) {
		if(index != 0 || inventoryItem == null) throw IllegalArgumentException("index out of bounds");
		selectedItem = inventoryItem;
	}

	override fun hasItem(item: Item): Boolean = (inventoryItem === item);

	override fun getInventory(): List<Item> = inventoryItem?.let { listOf<Item>(it) } ?: listOf<Item>();

	override fun clearInventory() {
		inventoryItem?.destroy();
	}

	fun getItem(): Item? = inventoryItem;
}
