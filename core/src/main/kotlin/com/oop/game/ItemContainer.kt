package com.oop.game;

abstract class ItemContainer(world: GameWorld, x: Float, y: Float, width: Float, height: Float) : GameObject(world, x, y, width, height) {
	open var containedItem: Item? = null;
	
	fun takeItem(gameObject: InventoryObject) {
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null) {
			// TODO 아이템이 없다는 메시지 표시
			return;
		}
		gameObject.addItemToInventory(target);
		containedItem = null;
	}
	
	override open fun update(delta: Float) {}
}
