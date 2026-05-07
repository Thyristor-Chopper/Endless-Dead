package com.oop.game;

import com.oop.game.InventoryObject;
import com.oop.game.LivingGameObject;

abstract class ItemContainer(x: Float, y: Float, width: Float, height: Float) : GameObject(x, y, width, height) {
	open var containedItem: Item? = null;
	
	fun takeItem(gameObject: InventoryObject) {
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null)
			throw IllegalStateException("아이템이 들어있지 않음");
		gameObject.addItemToInventory(target);
		containedItem = null;
	}
	
	override open fun update(delta: Float) {
	}
}
