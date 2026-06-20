package io.potatogun.gdxhelper.screen;

import com.badlogic.gdx.graphics.Color;

/**
 * '자막'을 그릴 수 있는 화면.
 *   자막이 어떤 모습으로 그려지는지는 구현체 나름이다.
 */
interface SubtitlesDrawable {
	// @JvmOverloads이 불가능해서 수동으로
	fun drawSubtitles(message: String) {
		drawSubtitles(message, DEFAULT_DURATION, DEFAULT_COLOR);
	}

	// @JvmOverloads이 불가능해서 수동으로
	fun drawSubtitles(message: String, color: Color) {
		drawSubtitles(message, DEFAULT_DURATION, color);
	}

	// @JvmOverloads이 불가능해서 수동으로
	fun drawSubtitles(message: String, duration: Int) {
		drawSubtitles(message, duration, DEFAULT_COLOR);
	}

	/**
	 * 화면에 자막을 표시한다.
	 *
	 * @param message  표시할 내용
	 * @param duration 표시 시간(초)
	 * @param color    글자 색
	 */
	fun drawSubtitles(message: String, duration: Int, color: Color);

	companion object {
		private const val DEFAULT_DURATION = 3;
		private val DEFAULT_COLOR = Color.WHITE;
	}
}
