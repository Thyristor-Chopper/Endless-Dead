package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.entity.DroppedItem;
import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.Math.max2;

import kotlin.random.Random;

/**
 * 아이템을 땅에 버릴 수 있는 개체
 *
 * @property entity 사용자
 */
class ItemDropComponent<T>(private val entity: T) where T : Entity, T : InventoryHolder {
	/**
	 * 인벤토리의 아이템을 버린다.
	 *
	 * @param item 버릴 아이템
	 * @return 성공 여부
	 */
	fun dropItem(item: Item): Boolean {
		if(!entity.inventory.hasItem(item)) return false;
		val world = entity.getWorld();
		val maxHalfLength = max2(entity.width, entity.height) * 0.5f;
		val spaceX = (Random.nextInt(4) + maxHalfLength + Constants.ITEM_SIZE * 0.5f) * if(Random.nextBoolean()) 1 else -1;
		val spaceY = (Random.nextInt(4) + maxHalfLength + Constants.ITEM_SIZE * 0.5f) * if(Random.nextBoolean()) 1 else -1;
		world.entities.add(DroppedItem(world, entity.x + spaceX, entity.y + spaceY, item));
		entity.inventory.removeItem(item);
		return true;
	}

	/**
	 * 인벤토리의 모든 아이템을 떨군다. (주로 죽었을 때 LivingEntity#takeDamage에서 호출)
	 *
	 * @return 성공 여부
	 */
	fun dropAll(): Boolean {
		entity.inventory.forEachItemsReverse { dropItem(it) };
		return true;
	}
}
