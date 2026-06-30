package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 이벤트 핸들러가 있는 인벤토리
 */
abstract class ObservableInventory : Inventory {
	private val itemAddObservers = GdxArray<(Item) -> Unit>(false, 4);
	private val itemRemoveObservers = GdxArray<(Item) -> Unit>(false, 4);
	private val clearObservers = GdxArray<() -> Unit>(false, 4);

	protected fun invokeItemAddObservers(item: Item) {
		for(i in 0 until itemAddObservers.size)
			itemAddObservers[i](item);
	}

	protected fun invokeItemRemoveObservers(item: Item) {
		for(i in 0 until itemRemoveObservers.size)
			itemRemoveObservers[i](item);
	}

	protected fun invokeClearObservers() {
		for(i in 0 until clearObservers.size)
			clearObservers[i]();
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
	 * 아이템이 추가될 때 호출되는 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백, Item: 추가된 아이템
	 */
	fun addItemAddObserver(handler: Consumer<Item>) {
		itemAddObservers.add { handler.accept(it) };
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
	 * 아이템이 제거될 때 호출되는 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백, Item: 제거된 아이템
	 */
	fun addItemRemoveObserver(handler: Consumer<Item>) {
		itemRemoveObservers.add { handler.accept(it) };
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	fun addClearObserver(handler: () -> Unit) {
		clearObservers.add(handler);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백
	 */
	fun addClearObserver(handler: Runnable) {
		clearObservers.add { handler.run() };
	}
}
