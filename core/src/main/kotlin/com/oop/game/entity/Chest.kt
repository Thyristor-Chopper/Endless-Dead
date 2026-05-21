package com.oop.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.oop.game.world.World;

class Chest(world: World, x: Float, y: Float): ItemContainer(world, x, y, 32.0f, 32.0f) {
	private val texture = Texture(Gdx.files.internal("chest.png"));
	
	override fun draw(batch: SpriteBatch) {
		batch.draw(texture, x, y, width, height);
	}
}
