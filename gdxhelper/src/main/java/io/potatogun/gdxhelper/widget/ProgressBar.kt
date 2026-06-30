package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.gdxhelper.HelperTextures;

import java.util.function.Supplier;

import kotlin.math.ceil;

/**
 * 진행률 표시기(미터기) - 코틀린용 생성자이며 자바 개발자라면 빌더를 사용하면 된다.
 *
 * x과 y 위치는 screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 *
 * @constructor 동적 위치를 사용하는 생성자
 * @param    x      X 좌표 계산 함수
 * @param    y      Y 좌표 계산 함수
 * @param    width  미터기 너비 계산 함수
 * @param    height 미터기 높이 계산 함수
 * @property value  미터기의 값(진행률) (0.0~1.0)
 * @property color  미터기의 색
 * @param    skin   미터기의 스킨(텍스처 묶음)
 * @property style  미터기의 스타일
 * @throws IllegalArgumentException 미터기 값이 잘못된 경우
 */
class ProgressBar(x: () -> Float, y: () -> Float, width: () -> Float, height: () -> Float = { 15f }, value: Float = 0f, skin: Skin? = null, private val color: Color = Color.WHITE, private val style: Style = Style.SMOOTH) : Widget(x, y, width, height) {
	companion object {
		private const val BAR_VERTICAL_PADDING = 3f;		// 미터기 틀 안쪽 세로 여백
		private const val BAR_HORIZONTAL_PADDING = 3f;	// 미터기 틀 안쪽 가로 여백
		private const val CHUNK_WIDTH = 6f;				// 청크의 너비
		private const val CHUNK_MARGIN = 2f;				// 각 청크 사이의 간격
		/**
		 * 프레임워크에서 제공하는 기본 스킨 (smooth)
		 */
		private val defaultSmoothSkin = Skin(HelperTextures.progressBar, HelperTextures.progressSmoothFill);
		/**
		 * 프레임워크에서 제공하는 기본 스킨 (chunked)
		 */
		private val defaultChunkedSkin = Skin(HelperTextures.progressBar, HelperTextures.progressChunkedFill);
	}

	var value: Float = value
		set(value) {
			if(value < 0f) field = 0f;
			else if(value > 1f) field = 1f;
			else field = value;
		};
	private val skin: Skin;

	/**
	 * 진행률 표시기(미터기) - 코틀린용 생성자이며 자바 개발자라면 빌더를 사용하면 된다.
	 *
	 * @constructor 정적 위치를 사용하는 생성자
	 * @param x      X 좌표
	 * @param y      Y 좌표
	 * @param width  미터기 너비
	 * @param height 미터기 높이
	 * @param value  미터기의 값(진행률) (0.0~1.0)
	 * @param color  미터기의 색
	 * @param skin   미터기의 스킨(텍스처 묶음)
	 * @param style  미터기의 스타일
	 * @throws IllegalArgumentException 미터기 값이 잘못된 경우
	 */
	constructor(x: Float, y: Float, width: Float, height: Float = 15f, value: Float = 0f, skin: Skin? = null, color: Color = Color.WHITE, style: Style = Style.SMOOTH) : this({ x }, { y }, { width }, { height }, value, skin, color, style);

	init {
		if(value < 0f || value > 1f)
			throw IllegalArgumentException("invalid progress bar value");

		if(skin == null)
			this.skin = if(style == Style.CHUNKED) defaultChunkedSkin else defaultSmoothSkin;
		else
			this.skin = skin;
	}

	override fun draw(batch: SpriteBatch) {
		val barX = calculateX();
		val barY = calculateY();
		val barWidth = calculateWidth();
		val barHeight = calculateHeight();
		skin.bar.draw(batch, barX, barY, barWidth, barHeight);
		if(value > 0f) {
			batch.color = color;
			val maxFillWidth = barWidth - BAR_HORIZONTAL_PADDING * 2;
			val fillWidth = maxFillWidth * value;
			val fillHeight = barHeight - BAR_VERTICAL_PADDING * 2;
			val fillX = barX + BAR_HORIZONTAL_PADDING;
			val fillY = barY + BAR_VERTICAL_PADDING;
			when(style) {
				Style.CHUNKED	-> {
					val chunkCount = ceil(fillWidth / (CHUNK_WIDTH + CHUNK_MARGIN)).toInt();
					for(i in 1..chunkCount) {
						val chunkX = fillX + (CHUNK_WIDTH + CHUNK_MARGIN) * (i - 1);  // 현재 청크의 X 위치
						val accumulatedWidth = chunkX - barX + CHUNK_WIDTH - CHUNK_MARGIN - 1;  // 누적된 청크 너비
						val chunkWidth = 
							if(i == chunkCount && accumulatedWidth > maxFillWidth)
								CHUNK_WIDTH - (accumulatedWidth - maxFillWidth)
							else
								CHUNK_WIDTH;
						skin.fill.draw(batch, chunkX, fillY, chunkWidth, fillHeight);
					}
				}
				Style.SMOOTH	-> {
					skin.fill.draw(batch, fillX, fillY, fillWidth, fillHeight);
				}
			}
			batch.color = Color.WHITE;
		}
	}

	/**
	 * 미터기(진행률 표시기)의 스킨이다.
	 *
	 * @property bar  미터기 틀의 9-patch 텍스처
	 * @property fill 채움 9-patch 텍스처
	 */
	data class Skin(@JvmField val bar: NinePatch, @JvmField val fill: NinePatch);

	/**
	 * 진행률 표시기(미터기) 스타일
	 */
	enum class Style {
		/**
		 * 윈도우 7처럼 연속적인 스타일
		 */
		SMOOTH,
		/**
		 * 윈도우 XP처럼 조각난 스타일
		 */
		CHUNKED;
	}

	/**
	 * 미터기 빌더 (자바 개발자 전용)
	 *
	 * @constructor 동적 크기를 사용하는 위젯
	 * @param x      X 좌표 계산 함수
	 * @param y      Y 좌표 계산 함수
	 * @param width  미터기 너비 계산 함수
	 * @param height 미터기 높이 계산 함수
	 */
	class Builder(private val x: Supplier<Float>, private val y: Supplier<Float>, private val width: Supplier<Float>, private val height: Supplier<Float>) {
		private var value = 0f;
		private var skin: Skin? = null;
		private var color = Color.WHITE;
		private var style = Style.SMOOTH;

		/**
		 * 미터기 빌더 (자바 개발자 전용)
		 *
		 * @constructor 정적 크기를 사용하는 위젯
		 * @param x      X 좌표
		 * @param y      Y 좌표
		 * @param width  미터기 너비
		 * @param height 미터기 높이
		 */
		constructor(x: Float, y: Float, width: Float, height: Float) : this({ x }, { y }, { width }, { height });

		fun value(value: Float): Builder {
			this.value = value;
			return this;
		}

		fun skin(skin: Skin): Builder {
			this.skin = skin;
			return this;
		}

		fun color(color: Color): Builder {
			this.color = color;
			return this;
		}

		fun style(style: Style): Builder {
			this.style = style;
			return this;
		}

		fun build(): ProgressBar {
			if(skin == null)
				skin = if(style == Style.CHUNKED) defaultChunkedSkin else defaultSmoothSkin;
			return ProgressBar({ x.get() }, { y.get() }, { width.get() }, { height.get() }, value, skin, color, style);
		}
	}
}
