package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;

/**
 * 이벤트 핸들러가 있는 인벤토리
 */
abstract class ObservableInventory : Inventory {
	private val itemAddObservers = mutableListOf<(Item) -> Unit>();
	private val itemRemoveObservers = mutableListOf<(Item) -> Unit>();
	private val clearObservers = mutableListOf<() -> Unit>();

	protected fun invokeItemAddObservers(item: Item) {
		itemAddObservers.forEach { it(item) };
	}

	protected fun invokeItemRemoveObservers(item: Item) {
		itemRemoveObservers.forEach { it(item) };
	}

	protected fun invokeClearObservers() {
		clearObservers.forEach { it() };
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백, Item: 추가된 아이템
	 */
	fun addItemAddObserver(handler: (Item) -> Unit) {
		itemAddObservers.add(handler);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백, Item: 제거된 아이템
	 */
	fun addItemRemoveObserver(handler: (Item) -> Unit) {
		itemRemoveObservers.add(handler);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	fun addClearObserver(handler: () -> Unit) {
		clearObservers.add(handler);
	}
}
