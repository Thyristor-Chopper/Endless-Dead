package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.entity.DroppedItem;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 떨어진 아이템을 주울 수 있는 개체
 */
interface ItemPickupable {
	/**
	 * 현재 떨어진 아이템을 주울 수 있는지의 여부
	 */
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("canPickupItems")
	val canPickupItems: Boolean;

	/**
	 * 주변에 떨어진 아이템을 모두 줍는다.
	 *
	 * @return 성공 여부
	 */
	fun pickupNearbyItems(): Boolean {
		if(!canPickupItems) return false;
		if(this !is Entity) return false;
		if(this !is InventoryHolder) return false;
		var pickedUp = false;
		getWorld().entities.forEachNearby(this) { entity ->
			if(entity !is DroppedItem) return@forEachNearby;
			if(!collidesWith(entity)) return@forEachNearby;
			val item = entity.item;
			entity.pickup(this);
			pickedUp = true;
			if(this is ItemSelectable && selectedItem == null)
				selectItem(item);
		};
		return pickedUp;
	}

	/**
	 * 지정한 아이템이 주변에 떨어져 있으면 줍는다.
	 *
	 * @return 성공 여부
	 */
	fun pickupItem(item: Item): Boolean {
		if(!canPickupItems) return false;
		if(this !is Entity) return false;
		if(this !is InventoryHolder) return false;
		val canSelectItem = this is ItemSelectable;
		var found = false;
		getWorld().entities.forEachNearby(this) { entity ->
			if(entity !is DroppedItem) return@forEachNearby;
			if(!collidesWith(entity)) return@forEachNearby;
			val droppedItem = entity.item;
			if(item === droppedItem) {
				entity.pickup(this);
				found = true;
				if(this is ItemSelectable && selectedItem == null)
					selectItem(droppedItem);
			}
		};
		return found;
	}
}
