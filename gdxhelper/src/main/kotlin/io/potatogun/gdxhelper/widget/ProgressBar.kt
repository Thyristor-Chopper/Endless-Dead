package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.gdxhelper.HelperTextures;
import io.potatogun.gdxhelper.widget.style.ProgressBarStyle;

import kotlin.math.ceil;

private const val BAR_VERTICAL_PADDING = 3f;		// 미터기 틀 안쪽 세로 여백
private const val BAR_HORIZONTAL_PADDING = 3f;	// 미터기 틀 안쪽 가로 여백
private const val CHUNK_WIDTH = 6f;				// 청크의 너비
private const val CHUNK_MARGIN = 2f;				// 각 청크 사이의 간격

/**
 * 진행률 표시기(미터기)
 *
 * x과 y 위치는 screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 *
 * @param x			X 좌표 계산 함수
 * @param y			Y 좌표 계산 함수
 * @param width		미터기 너비
 * @param height	미터기 높이
 * @param value		미터기의 값(진행률) (0.0~1.0)
 * @param color		미터기의 색
 * @param style		미터기의 스타일
 */
class ProgressBar(x: () -> Float, y: () -> Float, width: Float, height: Float = 15f, var value: Float = 0f, val color: Color = Color.WHITE, val style: ProgressBarStyle = ProgressBarStyle.SMOOTH) : Widget(x, y, width, height) {
	// 크기 조절 가능한 미터기 틀
	private val bar = HelperTextures.progressBar;
	// 크기 조절 가능한 미터기를 채워줄 친구
	private val fill: NinePatch = when(style) {
		ProgressBarStyle.CHUNKED	-> HelperTextures.progressChunkedFill
		ProgressBarStyle.SMOOTH		-> HelperTextures.progressSmoothFill
	};

	init {
		if(value < 0f || value > 1f)
			throw IllegalArgumentException("invalid progress bar value");
	}

	override fun draw(batch: SpriteBatch) {
		val barX = x();
		val barY = y();
		bar.draw(batch, barX, barY, width, height);
		if(value > 0f) {
			batch.color = color;
			val maxFillWidth = width - BAR_HORIZONTAL_PADDING * 2;
			val fillWidth = maxFillWidth * value;
			val fillHeight = height - BAR_VERTICAL_PADDING * 2;
			val fillX = barX + BAR_HORIZONTAL_PADDING;
			val fillY = barY + BAR_VERTICAL_PADDING;
			when(style) {
				ProgressBarStyle.CHUNKED	-> {
					val chunkCount = ceil(fillWidth / (CHUNK_WIDTH + CHUNK_MARGIN)).toInt();
					for(i in 1..chunkCount) {
						val chunkX = fillX + (CHUNK_WIDTH + CHUNK_MARGIN) * (i - 1);  // 현재 청크의 X 위치
						val accumulatedWidth = chunkX - barX + CHUNK_WIDTH - CHUNK_MARGIN - 1;  // 누적된 청크 너비
						val chunkWidth = 
							if(i == chunkCount && accumulatedWidth > maxFillWidth)
								CHUNK_WIDTH - (accumulatedWidth - maxFillWidth)
							else
								CHUNK_WIDTH;
						fill.draw(batch, chunkX, fillY, chunkWidth, fillHeight);
					}
				}
				ProgressBarStyle.SMOOTH		-> {
					fill.draw(batch, fillX, fillY, fillWidth, fillHeight);
				}
			}
			batch.color = Color.WHITE;
		}
	}
}
