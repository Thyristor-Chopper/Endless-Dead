package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.Pools;
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
	 * 주변에 떨어진 아이템 중 조건에 맞는 걸 줍는다.
	 *
	 * @param condition 줍기 조건
	 * @return 성공 여부
	 */
	@JvmOverloads fun pickupNearbyItems(condition: Predicate<Item>? = null): Boolean {
		var pickedUp = false;
		val nearbyEntities = Pools.entityArray.obtain();
		entity.getWorld().entities.getNearby(entity, nearbyEntities);
		for(i in 0 until nearbyEntities.size) {
			val e = nearbyEntities[i];
			if(e !is DroppedItem) continue;
			if(!entity.collidesWith(e)) continue;
			if(condition?.test(e.item) ?: true) {
				e.pickup(entity);
				pickedUp = true;
			}
		}
		return pickedUp;
	}

	/**
	 * 지정한 아이템이 주변에 떨어져 있으면 줍는다.
	 *
	 * @return 성공 여부
	 */
	fun pickupItem(item: Item): Boolean {
		var found = false;
		val nearbyEntities = Pools.entityArray.obtain();
		entity.getWorld().entities.getNearby(entity, nearbyEntities);
		for(i in 0 until nearbyEntities.size) {
			val e = nearbyEntities[i];
			if(e !is DroppedItem) continue;
			if(!entity.collidesWith(e)) continue;
			val droppedItem = e.item;
			if(item === droppedItem) {
				e.pickup(entity);
				found = true;
			}
		}
		return found;
	}
}
