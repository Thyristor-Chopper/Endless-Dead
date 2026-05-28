package com.oop.game.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract class Widget(val x: Float, val y: Float, val width: Float, val height: Float) {
	var visible = true;

	abstract fun draw(batch: SpriteBatch);
	
	abstract fun dispose();
}
