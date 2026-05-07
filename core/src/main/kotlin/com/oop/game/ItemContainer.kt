package com.oop.game;

import com.oop.game.LivingGameObject;

abstract class ItemContainer(x: Float, y: Float, width: Float, height: Float) : GameObject(x, y, width, height) {
	open var containedItem: Item? = null;
}
