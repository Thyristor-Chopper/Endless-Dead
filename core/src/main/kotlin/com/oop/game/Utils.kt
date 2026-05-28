package com.oop.game;

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
}
