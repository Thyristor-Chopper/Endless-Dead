package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.item.Item;

/**
 * 인벤토리의 기본적인 구현체
 *
 * @property maxSlots 최대 아이템 개수(-1: 무제한)
 * @throws IllegalArgumentException 최대 아이템 개수가 잘못된 경우
 */
class LinearInventory(override val maxSlots: Int = -1) : ObservableInventory() {
	private val inventory = mutableListOf<Item>();
	override val itemCount: Int
		get() = inventory.size;
	override val isEmpty: Boolean
		get() = inventory.isEmpty();

	init {
		if(maxSlots < -1)
			throw IllegalArgumentException("maxSlots must be positive, zero or -1");
	}

	override fun addItem(item: Item): Boolean {
		if(maxSlots != -1 && inventory.size >= maxSlots) return false;
		if(hasItem(item)) return false;
		val holder: Inventory? = item.inventory;
		if(!(holder?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가
		inventory.add(item);
		item.inventory = this;
		invokeItemAddObservers(item);
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		if(index < 0 || index >= inventory.size) return false;
		val item = inventory[index];
		inventory.removeAt(index);
		item.inventory = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun removeItem(item: Item): Boolean {
		val index = inventory.indexOfFirst({ it === item });
		if(index == -1) return false;
		inventory.removeAt(index);
		item.inventory = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun getItem(index: Int): Item = inventory.getOrNull(index) ?: throw IndexOutOfBoundsException("index out of bounds");

	override fun hasItem(item: Item): Boolean = inventory.any { it === item };

	override fun indexOf(item: Item): Int = inventory.indexOfFirst({ it === item });

	override fun getItems(): List<Item> = inventory.toList();

	override fun clear() {
		for(item in inventory)
			item.inventory = null;
		inventory.clear();
		invokeClearObservers();
	}
}
