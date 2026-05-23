package com.oop.game.entity.container;

import com.oop.game.entity.Entity;
import com.oop.game.entity.InventoryEntity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

/**
 * 아이템 상자 역할을 하는 추상 클래스
 */
abstract class Container(world: World, x: Float, y: Float, width: Float, height: Float, texture: String) : Entity(world, x, y, width, height, texture) {
	open var containedItem: Item? = null;  // 들어있는 아이템
	val isEmpty: Boolean
		get() = (containedItem == null);
	
	/**
	 * 아이템 가져가기
	 *
	 * @param taker	아이템을 가져가는 인벤토리를 가진 개체
	 */
	fun takeItem(taker: InventoryEntity) {
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null) {
			// TODO 아이템이 없다는 메시지 표시
			return;
		}
		taker.addItemToInventory(target);
		containedItem = null;
	}
}
