package com.oop.game.entity;

import com.oop.game.item.Item;
import com.oop.game.world.World;

abstract class ItemContainer(world: World, x: Float, y: Float, width: Float, height: Float) : Entity(world, x, y, width, height) {
	open var containedItem: Item? = null;
	
	fun takeItem(taker: InventoryEntity) {
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null) {
			// TODO 아이템이 없다는 메시지 표시
			return;
		}
		taker.addItemToInventory(target);
		containedItem = null;
	}
	
	override open fun update(delta: Float) {}
}
