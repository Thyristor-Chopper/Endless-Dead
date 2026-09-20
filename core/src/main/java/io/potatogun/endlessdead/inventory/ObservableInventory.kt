package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 이벤트 핸들러가 있는 인벤토리
 */
abstract class ObservableInventory : Inventory {
	private val addObservers = GdxArray<Consumer<Item>>(false, 2);
	private val removeObservers = GdxArray<Consumer<Item>>(false, 2);
	private val clearObservers = GdxArray<Runnable>(false, 2);

	/**
	 * 아이템 추가 이벤트 핸들러를 실행한다.
	 */
	protected fun invokeAddObservers(item: Item) {
		for(i in 0 until addObservers.size)
			addObservers[i].accept(item);
	}

	/**
	 * 아이템 제거 이벤트 핸들러를 실행한다.
	 */
	protected fun invokeRemoveObservers(item: Item) {
		for(i in 0 until removeObservers.size)
			removeObservers[i].accept(item);
	}

	/**
	 * 인벤토리 초기화 이벤트 핸들러를 실행한다.
	 */
	protected fun invokeClearObservers() {
		for(i in 0 until clearObservers.size)
			clearObservers[i].run();
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백, Item: 추가된 아이템
	 */
	fun attachAddObserver(handler: Consumer<Item>) {
		addObservers.add(handler);
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun detachAddObserver(handler: Consumer<Item>) {
		addObservers.removeValue(handler, true);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 지정한다. (전체 clear 시에는 호출되지 않음에 주의)
	 *
	 * @param handler 콜백, Item: 제거된 아이템
	 */
	fun attachRemoveObserver(handler: Consumer<Item>) {
		removeObservers.add(handler);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun detachRemoveObserver(handler: Consumer<Item>) {
		removeObservers.removeValue(handler, true);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	fun attachClearObserver(handler: Runnable) {
		clearObservers.add(handler);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun detachClearObserver(handler: Runnable) {
		clearObservers.removeValue(handler, true);
	}
}
