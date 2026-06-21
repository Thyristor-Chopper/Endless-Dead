package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.inventory.Inventory;

/**
 * 인벤토리를 가진 개체에 대한 인터페이스
 */
interface InventoryEntity {
	val inventory: Inventory;
}
