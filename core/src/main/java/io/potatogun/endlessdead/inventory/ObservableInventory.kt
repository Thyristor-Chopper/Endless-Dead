package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.ObjectMap;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 이벤트 핸들러가 있는 인벤토리
 */
abstract class ObservableInventory : Inventory {
	private val itemAddObservers = GdxArray<Consumer<Item>>(false, 2);
	private val itemRemoveObservers = GdxArray<Consumer<Item>>(false, 2);
	private val clearObservers = GdxArray<Runnable>(false, 2);

	protected fun invokeItemAddObservers(item: Item) {
		for(i in 0 until itemAddObservers.size)
			itemAddObservers[i].accept(item);
	}

	protected fun invokeItemRemoveObservers(item: Item) {
		for(i in 0 until itemRemoveObservers.size)
			itemRemoveObservers[i].accept(item);
	}

	protected fun invokeClearObservers() {
		for(i in 0 until clearObservers.size)
			clearObservers[i].run();
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백, Item: 추가된 아이템
	 */
	fun addItemAddObserver(handler: Consumer<Item>) {
		itemAddObservers.add(handler);
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeItemAddObserver(handler: Consumer<Item>) {
		itemAddObservers.removeValue(handler, true);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백, Item: 제거된 아이템
	 */
	fun addItemRemoveObserver(handler: Consumer<Item>) {
		itemRemoveObservers.add(handler);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeItemRemoveObserver(handler: Consumer<Item>) {
		itemRemoveObservers.removeValue(handler, true);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	fun addClearObserver(handler: Runnable) {
		clearObservers.add(handler);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeClearObserver(handler: Runnable) {
		clearObservers.removeValue(handler, true);
	}
}
