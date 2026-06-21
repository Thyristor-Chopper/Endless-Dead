package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 이벤트 핸들러가 있는 인벤토리
 */
abstract class ObservableInventory : Inventory {
	private val itemAddObservers = mutableListOf<(Item, Entity?) -> Unit>();
	private val itemRemoveObservers = mutableListOf<(Item) -> Unit>();
	private val clearObservers = mutableListOf<() -> Unit>();

	protected fun invokeItemAddObservers(item: Item, previousOwner: Entity?) {
		itemAddObservers.forEach { it(item, previousOwner) };
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
	 * @param handler 콜백, Item: 추가된 아이템, Entity?: 기존 소유자 (없으면 null)
	 */
	fun addItemAddObserver(handler: (Item, Entity?) -> Unit) {
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
