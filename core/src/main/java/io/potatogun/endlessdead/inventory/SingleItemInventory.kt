package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.item.Item;

/**
 * 하나의 아이템만 가질 수 있는 인벤토리 구현체
 */
class SingleItemInventory : ObservableInventory() {
	private var inventoryItem: Item? = null;
	override val itemCount: Int
		get() = if(inventoryItem != null) 1 else 0;
	override val isEmpty: Boolean
		get() = (inventoryItem == null);
	override val maxSlots = 1;

	override fun addItem(item: Item): Boolean {
		if(inventoryItem != null) return false;
		val holder: Inventory? = item.inventory;
		if(!(holder?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가
		inventoryItem = item;
		item.inventory = this;
		invokeItemAddObservers(item);
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		val item = inventoryItem;
		if(index != 0 || item == null) return false;
		inventoryItem = null;
		item.inventory = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun removeItem(item: Item): Boolean {
		if(inventoryItem !== item) return false;
		inventoryItem = null;
		item.inventory = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun getItem(index: Int): Item {
		val item: Item? = inventoryItem;
		if(index != 0 || item == null) throw IndexOutOfBoundsException("index out of bounds");
		return item;
	}

	override fun hasItem(item: Item): Boolean = (inventoryItem === item);

	override fun indexOf(item: Item): Int = if(inventoryItem === item) 0 else -1;

	override fun getItems(): GdxArray<Item> = inventoryItem?.let { GdxArray<Item>(false, 1).apply { add(it) } } ?: GdxArray<Item>(false, 0);

	override fun clear() {
		inventoryItem?.let {
			it.inventory = null;
			inventoryItem = null;
			invokeItemRemoveObservers(it);
		}
		invokeClearObservers();
	}

	/**
	 * 들어 있는 아이템을 반환한다.
	 *
	 * @return 현재 들어 있는 아이템
	 */
	fun getItem(): Item? = inventoryItem;

	/**
	 * 아이템을 교체한다.
	 *
	 * @param item 새 아이템
	 * @return     성공 여부
	 */
	fun replaceItem(item: Item): Boolean {
		if(inventoryItem == null) return false;
		val holder: Inventory? = item.inventory;
		if(!(holder?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가
		inventoryItem?.let {
			inventoryItem = item;
			invokeItemRemoveObservers(it);
		};
		item.inventory = this;
		invokeItemAddObservers(item);
		return true;
	}
}