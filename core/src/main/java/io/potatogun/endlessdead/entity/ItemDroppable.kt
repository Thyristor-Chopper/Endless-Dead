package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.entity.DroppedItem;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.Math.max2;

import kotlin.random.Random;

/**
 * 아이템을 땅에 버릴 수 있는 개체
 */
interface ItemDroppable {
	/**
	 * 현재 아이템을 땅에 버릴 수 있는지의 여부
	 */
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("canDropItems")
	val canDropItems: Boolean;

	/**
	 * 아이템을 버린다.
	 *
	 * @param item 버릴 아이템
	 * @return 성공 여부
	 */
	fun dropItem(item: Item): Boolean {
		if(!canDropItems) return false;
		if(this !is Entity) return false;
		if(this !is InventoryHolder) return false;
		if(!inventory.hasItem(item)) return false;
		val world = getWorld();
		val maxHalfLength = max2(width, height) * 0.5f;
		val spaceX = (Random.nextInt(4) + maxHalfLength + Constants.ITEM_SIZE * 0.5f) * if(Random.nextBoolean()) 1 else -1;
		val spaceY = (Random.nextInt(4) + maxHalfLength + Constants.ITEM_SIZE * 0.5f) * if(Random.nextBoolean()) 1 else -1;
		world.entities.add(DroppedItem(world, x + spaceX, y + spaceY, item));
		inventory.removeItem(item);
		return true;
	}
}
