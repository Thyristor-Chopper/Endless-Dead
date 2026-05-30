package com.oop.game;

import com.badlogic.gdx.graphics.Color;

/**
 * 유용한 함수 모음
 */
object Utils {
	/**
	 * 초를 X분 X초로 변환한다
	 *
	 * @param seconds	초
	 */
	fun parseSeconds(seconds: Int, minutesSuffix: String = " minute(s)", secondsSuffix: String = "second(s)"): String {
		if(seconds < 60) return "${seconds}$secondsSuffix";
		return "${seconds / 60}$minutesSuffix ${seconds % 60}$secondsSuffix";
	}
	
	inline fun rgb(r: Int, g: Int, b: Int, a: Float = 1.0f): Color {
		if(r > 255 || g > 255 || b > 255 || r < 0 || g < 0 || b < 0 || a > 1f || a < 0f)
			throw IllegalArgumentException("invalid color value");
		return Color(r / 255f, g / 255f, b / 255f, a);
	}
}
