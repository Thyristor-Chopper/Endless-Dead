package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.inventory.Inventory;

/**
 * 인벤토리가 있는 객체에 대한 인터페이스
 */
interface InventoryHolder {
	/**
	 * 할당되는 인벤토리 (자바에서는 getInventory()가 되겠지)
	 */
	val inventory: Inventory;
}
