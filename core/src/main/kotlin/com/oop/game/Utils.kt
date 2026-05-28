package com.oop.game;

/**
 * 유용한 함수 모음
 */
object Utils {
	const val PROGRESS_BAR_OPEN = '[';
	const val PROGRESS_BAR_CLOSE = ']';
	const val PROGRESS_BAR_FILLED = '=';
	const val PROGRESS_BAR_EMPTY = ' ';
	const val PROGRESS_BAR_CHARACTERS = "${PROGRESS_BAR_FILLED}${PROGRESS_BAR_EMPTY}";
	const val DEFAULT_PROGRESS_BAR_SIZE = 20;
	
	/**
	 * 미터기를 그린다.
	 *
	 * @param value	실수 진행률(0.0~1.0)
	 */
	inline fun progressBar(value: Float, size: Int = DEFAULT_PROGRESS_BAR_SIZE): String {
		return progressBar((value * 100f).toInt(), size);
	}
	
	/**
	 * 미터기를 그린다.
	 *
	 * @param value	정수 진행률(1~100)
	 */
	fun progressBar(value: Int, size: Int = DEFAULT_PROGRESS_BAR_SIZE): String {
		var ret = "$PROGRESS_BAR_OPEN";
		val filled = (value / 100f * size).toInt();
		for(i in 1..filled)
			ret += PROGRESS_BAR_FILLED;
		for(i in 1..(size - filled))
			ret += PROGRESS_BAR_EMPTY;
		ret += "$PROGRESS_BAR_CLOSE";
		return ret;
	}
	
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
