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
	private val itemAddObservers = GdxArray<(Item) -> Unit>(false, 2);
	private val javaAddObserverMap = ObjectMap<Consumer<Item>, (Item) -> Unit>(2);
	private val itemRemoveObservers = GdxArray<(Item) -> Unit>(false, 2);
	private val javaRemoveObserverMap = ObjectMap<Consumer<Item>, (Item) -> Unit>(2);
	private val clearObservers = GdxArray<() -> Unit>(false, 2);
	private val javaClearObserverMap = ObjectMap<Runnable, () -> Unit>(2);

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
	@JvmSynthetic fun addItemAddObserver(handler: (Item) -> Unit) {
		itemAddObservers.add(handler);
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백, Item: 추가된 아이템
	 */
	fun addItemAddObserver(handler: Consumer<Item>) {
		val ktHandler: (Item) -> Unit = handler::accept;
		javaAddObserverMap.put(handler, ktHandler);
		itemAddObservers.add(ktHandler);
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	@JvmSynthetic fun removeItemAddObserver(handler: (Item) -> Unit) {
		itemAddObservers.removeValue(handler, true);
	}

	/**
	 * 아이템이 추가될 때 호출되는 콜백 함수를 해제한다 (자바에서 사용).
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeItemAddObserver(handler: Consumer<Item>) {
		javaAddObserverMap[handler]?.let {
			itemAddObservers.removeValue(it, true);
			javaAddObserverMap.remove(handler);
		};
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백, Item: 제거된 아이템
	 */
	@JvmSynthetic fun addItemRemoveObserver(handler: (Item) -> Unit) {
		itemRemoveObservers.add(handler);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백, Item: 제거된 아이템
	 */
	fun addItemRemoveObserver(handler: Consumer<Item>) {
		val ktHandler: (Item) -> Unit = handler::accept;
		javaRemoveObserverMap.put(handler, ktHandler);
		itemRemoveObservers.add(ktHandler);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	@JvmSynthetic fun removeItemRemoveObserver(handler: (Item) -> Unit) {
		itemRemoveObservers.removeValue(handler, true);
	}

	/**
	 * 아이템이 제거될 때 호출되는 콜백 함수를 해제한다 (자바에서 사용).
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeItemRemoveObserver(handler: Consumer<Item>) {
		javaRemoveObserverMap[handler]?.let {
			itemRemoveObservers.removeValue(it, true);
			javaRemoveObserverMap.remove(handler);
		};
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	@JvmSynthetic fun addClearObserver(handler: () -> Unit) {
		clearObservers.add(handler);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백
	 */
	fun addClearObserver(handler: Runnable) {
		val ktHandler: () -> Unit = handler::run;
		javaClearObserverMap.put(handler, ktHandler);
		clearObservers.add(ktHandler);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	@JvmSynthetic fun removeClearObserver(handler: () -> Unit) {
		clearObservers.removeValue(handler, true);
	}

	/**
	 * 인벤토리가 초기화될 때 콜백 함수를 해제한다 (자바에서 사용).
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeClearObserver(handler: Runnable) {
		javaClearObserverMap[handler]?.let {
			clearObservers.removeValue(it, true);
			javaClearObserverMap.remove(handler);
		};
	}
}
