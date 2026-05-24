package com.oop.game;

object Utils {
	val PROGRESS_BAR_FILLED = '=';
	val PROGRESS_BAR_EMPTY = ' ';
	val DEFAULT_PROGRESS_BAR_SIZE = 20;
	
	/**
	 * 미터기를 그린다.
	 *
	 * @param value	실수 진행률(0.0~1.0)
	 */
	fun progressBar(value: Float, size: Int = DEFAULT_PROGRESS_BAR_SIZE): String {
		return progressBar((value * 100.0f).toInt(), size);
	}
	
	/**
	 * 미터기를 그린다.
	 *
	 * @param value	정수 진행률(1~100)
	 */
	fun progressBar(value: Int, size: Int = DEFAULT_PROGRESS_BAR_SIZE): String {
		var ret = "[";
		val filled = (value / 100.0f * size).toInt();
		for(i in 1..filled)
			ret += PROGRESS_BAR_FILLED;
		for(i in 1..(size - filled))
			ret += PROGRESS_BAR_EMPTY;
		ret += "]";
		return ret;
	}
	
	/**
	 * 초를 X분 X초로 변환한다
	 *
	 * @param seconds	초
	 */
	fun parseSeconds(seconds: Int): String {
		if(seconds < 60) return "${seconds}초";
		return "${seconds / 60}분 ${seconds % 60}초";
	}
}
