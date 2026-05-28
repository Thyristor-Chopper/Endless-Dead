package com.oop.game.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract class Widget(var x: () -> Float, var y: () -> Float, val width: Float, val height: Float) {
	var visible = true
		private set;

	abstract fun draw(batch: SpriteBatch);
	
	abstract fun dispose();
	
	fun hide() {
		visible = false;
	}
	
	fun show() {
		visible = true;
	}
}
