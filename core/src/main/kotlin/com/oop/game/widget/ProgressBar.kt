package com.oop.game.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.oop.game.widget.style.ProgressBarStyle;

import kotlin.math.ceil;

private const val BAR_VERTICAL_PADDING = 3;
private const val BAR_HORIZONTAL_PADDING = 3;
private const val CHUNK_WIDTH = 6;
private const val CHUNK_HEIGHT = 12;
private const val CHUNK_MARGIN = 2;

class ProgressBar(x: () -> Float, y: () -> Float, width: Float, height: Float = 15f, var value: Float = 0f, val color: Color = Color.WHITE, val style: ProgressBarStyle = ProgressBarStyle.CHUNKED) : Widget(x, y, width, height) {
	private val rawBarTexture = Texture(Gdx.files.internal("progress_bar.bmp"));
	private val rawChunkTexture = Texture(Gdx.files.internal("progress_chunk.bmp"));
	private val barTexture = NinePatch(rawBarTexture, 2, 2, 5, 6);
	private val indicatorTexture: NinePatch by lazy { NinePatch(rawChunkTexture, 1, 1, 1, 1) };
	private val chunkTexture: NinePatch by lazy { NinePatch(TextureRegion(rawChunkTexture, 1, 0, 1, CHUNK_HEIGHT), 0, 0, 1, 1) };
	
	override fun draw(batch: SpriteBatch) {
		val barX = x();
		val barY = y();
		barTexture.draw(batch, barX, barY, width, height);
		if(value > 0f) {
			batch.color = color;
			val maxIndicatorWidth = width - BAR_HORIZONTAL_PADDING * 2;
			val indicatorWidth = maxIndicatorWidth * value;
			val indicatorHeight = height - BAR_VERTICAL_PADDING * 2;
			val indicatorX = barX + BAR_HORIZONTAL_PADDING;
			val indicatorY = barY + BAR_VERTICAL_PADDING;
			when(style) {
				ProgressBarStyle.CHUNKED	-> {
					val chunkCount = ceil(indicatorWidth / (CHUNK_WIDTH + CHUNK_MARGIN)).toInt();
					for(i in 1..chunkCount) {
						val chunkX = indicatorX + (CHUNK_WIDTH + CHUNK_MARGIN) * (i - 1);
						val accumulatedWidth = chunkX - barX + CHUNK_WIDTH - CHUNK_MARGIN - 1;
						val chunkWidth = 
							if(i == chunkCount && accumulatedWidth > maxIndicatorWidth)
								CHUNK_WIDTH - (accumulatedWidth - maxIndicatorWidth)
							else
								CHUNK_WIDTH.toFloat();
						chunkTexture.draw(batch, chunkX, indicatorY, chunkWidth, indicatorHeight);
					}
				}
				ProgressBarStyle.SMOOTH		-> {
					indicatorTexture.draw(batch, indicatorX, indicatorY, indicatorWidth, indicatorHeight);
				}
			}
			batch.color = Color.WHITE;
		}
	}
	
	override fun dispose() {
		rawBarTexture.dispose();
		rawChunkTexture.dispose();
	}
}
