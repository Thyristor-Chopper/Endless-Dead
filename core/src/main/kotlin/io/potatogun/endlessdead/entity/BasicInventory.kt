package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 인벤토리를 가진 개체의 기본적인 구현체.
 * 단독으로 사용하지 않고 위임으로만 사용된다.
 *
 * @param maxSlots 최대 아이템 개수(-1: 무제한)
 */
class BasicInventory(override val maxSlots: Int = -1) : InventoryEntity {
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
	override val itemCount: Int
		get() = inventory.size;
	override val isInventoryEmpty: Boolean
		get() = inventory.isEmpty();
	override val firstItem: Item?
		get() = inventory.getOrNull(0);
	override val lastItem: Item?
		get() = inventory.getOrNull(inventory.size - 1);

	init {
		if(maxSlots < -1)
			throw IllegalArgumentException("maxSlots must be positive, zero or -1");
	}

	override fun addItem(item: Item, select: Boolean): Boolean {
		if(maxSlots != -1 && inventory.size >= maxSlots) return false;
		if(hasItem(item)) return false;
		val holder: Entity? = item.holder;
		if(holder is InventoryEntity)
			holder.removeItem(item);
		inventory.add(item);
		if(select) selectedItemIndex = inventory.size - 1;
		return true;
	}

	override fun removeItem(index: Int) {
		if(index < 0 || index >= inventory.size) throw IllegalArgumentException("index out of bounds");
		val currentIndex: Int? = selectedItemIndex;
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == currentIndex)
			selectPreviousItem();
	}

	override fun removeItem(item: Item): Boolean {
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

	override fun getItem(index: Int): Item = inventory.getOrNull(index) ?: throw IllegalArgumentException("index out of bounds");

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

	override fun clearInventory() {
		inventory.forEach { it.destroy() };
		inventory.clear();
	}
}
