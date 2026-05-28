package com.oop.game.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract class Widget(var x: Float, var y: Float, val width: Float, val height: Float) : WorldObject {
	var visible = true;

	abstract fun draw(batch: SpriteBatch);
	
	abstract fun dispose();
}
