package com.oop.game.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

private const val BAR_VERTICAL_PADDING = 3;
private const val BAR_HORIZONTAL_PADDING = 3;

class ProgressBar(x: Float, y: Float, width: Float, height: Float = 15f, var value: Float = 0f, val color: Color = Color.WHITE) : Widget(x, y, width, height) {
	private val rawBarTexture = Texture(Gdx.files.internal("progress_bar.bmp"));
	private val rawIndicatorTexture = Texture(Gdx.files.internal("progress_chunk.bmp"));
	private val barTexture = NinePatch(rawBarTexture, 2, 2, 5, 6);
	private val indicatorTexture = NinePatch(rawIndicatorTexture, 0, 0, 2, 1);
	
	override fun draw(batch: SpriteBatch) {
		if(!visible) return;
		barTexture.draw(batch, x, y, width, height);
		batch.color = color;
		indicatorTexture.draw(batch, x + BAR_HORIZONTAL_PADDING, y + BAR_VERTICAL_PADDING, (width - BAR_HORIZONTAL_PADDING * 2) * value, height - BAR_VERTICAL_PADDING * 2);
		batch.color = Color.WHITE;
	}
	
	override fun dispose() {
		rawBarTexture.dispose();
		rawIndicatorTexture.dispose();
	}
}
