package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 하나의 아이템만 가질 수 있는 인벤토리 구현체
 */
class SingleItemInventory : InventoryObserverAdapter() {
	private var inventoryItem: Item? = null;
	override val itemCount: Int
		get() = if(inventoryItem != null) 1 else 0;
	override val isEmpty: Boolean
		get() = (inventoryItem == null);
	override val maxSlots = 1;
	override val firstItem: Item?
		get() = inventoryItem;
	override val lastItem: Item?
		get() = inventoryItem;

	override fun addItem(item: Item): Boolean {
		if(inventoryItem != null) return false;
		val holder: Entity? = item.holder;
		if(holder is InventoryEntity)
			holder.inventory.removeItem(item);
		inventoryItem = item;
		itemAddObservers.forEach { it(item, holder) };
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		val item = inventoryItem;
		if(index != 0 || item == null) return false;
		inventoryItem = null;
		itemRemoveObservers.forEach { it(item) };
		return true;
	}

	override fun removeItem(item: Item): Boolean {
		if(inventoryItem !== item) return false;
		inventoryItem = null;
		itemRemoveObservers.forEach { it(item) };
		return true;
	}

	override fun getItem(index: Int): Item {
		val item: Item? = inventoryItem;
		if(index != 0 || item == null) throw IllegalArgumentException("index out of bounds");
		return item;
	}

	override fun hasItem(item: Item): Boolean = (inventoryItem === item);

	override fun indexOf(item: Item): Int = if(inventoryItem === item) 0 else -1;

	override fun getInventory(): List<Item> = inventoryItem?.let { listOf<Item>(it) } ?: listOf<Item>();

	override fun clear() {
		inventoryItem?.destroy();
		clearObservers.forEach { it() };
	}

	fun getItem(): Item? = inventoryItem;
}