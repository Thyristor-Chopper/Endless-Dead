package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.entity.DroppedItem;
import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;

import java.util.function.Predicate;

/**
 * 떨어진 아이템을 주울 수 있는 개체
 *
 * @property entity 사용자
 */
class ItemPickupComponent<T>(private val entity: T) where T : Entity, T : InventoryHolder {
	/**
	 * 주변에 떨어진 아이템을 모두 줍는다.
	 *
	 * @return 성공 여부
	 */
	fun pickupNearbyItems(): Boolean {
		var pickedUp = false;
		entity.forEachNearby { e ->
			if(e !is DroppedItem) return@forEachNearby;
			if(!entity.collidesWith(e)) return@forEachNearby;
			e.pickup(entity);
			pickedUp = true;
		};
		return pickedUp;
	}

	/**
	 * 주변에 떨어진 아이템 중 조건에 맞는 걸 줍는다.
	 *
	 * @param condition 조건
	 * @return 성공 여부
	 */
	fun pickupNearbyItems(condition: Predicate<Item>): Boolean {
		var pickedUp = false;
		entity.forEachNearby { e ->
			if(e !is DroppedItem) return@forEachNearby;
			if(!entity.collidesWith(e)) return@forEachNearby;
			if(condition.test(e.item)) {
				e.pickup(entity);
				pickedUp = true;
			}
		};
		return pickedUp;
	}

	/**
	 * 지정한 아이템이 주변에 떨어져 있으면 줍는다.
	 *
	 * @return 성공 여부
	 */
	fun pickupItem(item: Item): Boolean {
		var found = false;
		entity.forEachNearby { e ->
			if(e !is DroppedItem) return@forEachNearby;
			if(!entity.collidesWith(e)) return@forEachNearby;
			val droppedItem = e.item;
			if(item === droppedItem) {
				e.pickup(entity);
				found = true;
			}
		};
		return found;
	}
}
