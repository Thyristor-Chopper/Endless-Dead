package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.inventory.Inventory;
import io.potatogun.endlessdead.item.Item;

/**
 * 인벤토리가 있는 객체에 대한 인터페이스
 */
interface InventoryHolder {
	/**
	 * 할당되는 인벤토리
	 *
	 * 자바에서는 getInventory() 사용
	 */
	val inventory: Inventory;
}
