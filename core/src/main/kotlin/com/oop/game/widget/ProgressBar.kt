package com.oop.game.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kotlin.math.ceil;

private const val BAR_VERTICAL_PADDING = 3;
private const val BAR_HORIZONTAL_PADDING = 3;
private const val CHUNK_WIDTH = 6;
private const val CHUNK_MARGIN = 2;

class ProgressBar(x: () -> Float, y: () -> Float, width: Float, height: Float = 15f, var value: Float = 0f, val color: Color = Color.WHITE) : Widget(x, y, width, height) {
	var style = ProgressBarStyle.CHUNKED;
	private val rawBarTexture = Texture(Gdx.files.internal("progress_bar.bmp"));
	private val rawIndicatorTexture = Texture(Gdx.files.internal("progress_indicator.bmp"));
	private val chunkTexture = Texture(Gdx.files.internal("progress_chunk.bmp"));
	private val barTexture = NinePatch(rawBarTexture, 2, 2, 5, 6);
	private val indicatorTexture = NinePatch(rawIndicatorTexture, 1, 1, 2, 1);
	
	override fun draw(batch: SpriteBatch) {
		if(!visible) return;
		val x = this.x();
		val y = this.y();
		barTexture.draw(batch, x, y, width, height);
		if(value > 0f) {
			batch.color = color;
			val drawableBarWidth = (width - BAR_HORIZONTAL_PADDING * 2);
			val indicatorWidth = drawableBarWidth * value;
			when(style) {
				ProgressBarStyle.CHUNKED	-> {
					val chunkCount = ceil(indicatorWidth / (CHUNK_WIDTH + CHUNK_MARGIN)).toInt();
					for(i in 1..chunkCount) {
						val left = x + BAR_HORIZONTAL_PADDING + (CHUNK_WIDTH + CHUNK_MARGIN) * (i - 1);
						val width = 
							if(i == chunkCount && left - x + CHUNK_WIDTH - CHUNK_MARGIN - 1 > drawableBarWidth)
								CHUNK_WIDTH - (left - x + CHUNK_WIDTH - CHUNK_MARGIN - 1 - drawableBarWidth)
							else
								CHUNK_WIDTH.toFloat();
						batch.draw(chunkTexture, left, y + BAR_VERTICAL_PADDING, width, height - BAR_VERTICAL_PADDING * 2);
					}
				}
				ProgressBarStyle.SMOOTH		-> {
					indicatorTexture.draw(batch, x + BAR_HORIZONTAL_PADDING, y + BAR_VERTICAL_PADDING, indicatorWidth, height - BAR_VERTICAL_PADDING * 2);
				}
			}
			batch.color = Color.WHITE;
		}
	}
	
	override fun dispose() {
		rawBarTexture.dispose();
		chunkTexture.dispose();
		rawIndicatorTexture.dispose();
	}
}
