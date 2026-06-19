package io.potatogun.gdxhelper.screen;

import com.badlogic.gdx.graphics.Color;

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
	 * @param message	표시할 내용
	 * @param duration	표시 시간(초)
	 * @param color		글자 색
	 */
	fun drawSubtitles(message: String, duration: Int, color: Color);

	companion object {
		private const val DEFAULT_DURATION = 3;
		private val DEFAULT_COLOR = Color.WHITE;
	}
}
