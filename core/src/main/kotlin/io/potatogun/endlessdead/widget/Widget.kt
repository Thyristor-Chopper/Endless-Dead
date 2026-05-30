package com.oop.game.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract class Widget(var x: () -> Float, var y: () -> Float, val width: Float, val height: Float) {
	var isVisible = true
		private set;

	abstract fun draw(batch: SpriteBatch);
	
	open fun dispose() {}
	
	fun hide() {
		isVisible = false;
	}
	
	fun show() {
		isVisible = true;
	}
}
