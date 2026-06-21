package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 인벤토리 인터페이스 중 이벤트 핸들러 등록 부분만 구현한다.
 */
abstract class InventoryObserverAdapter : Inventory {
	protected val itemAddObservers = mutableListOf<(Item, Entity?) -> Unit>();
	protected val itemRemoveObservers = mutableListOf<(Item) -> Unit>();
	protected val clearObservers = mutableListOf<() -> Unit>();

	override fun addItemAddObserver(handler: (Item, Entity?) -> Unit) {
		itemAddObservers.add(handler);
	}

	override fun addItemRemoveObserver(handler: (Item) -> Unit) {
		itemRemoveObservers.add(handler);
	}

	override fun addClearObserver(handler: () -> Unit) {
		clearObservers.add(handler);
	}
}
