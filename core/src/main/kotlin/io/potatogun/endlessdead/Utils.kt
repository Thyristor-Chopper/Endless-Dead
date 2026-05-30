package io.potatogun.endlessdead;

import com.badlogic.gdx.graphics.Color;

import io.potatogun.endlessdead.Timer;

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
	
	/**
	 * R, G, B 값을 받아 Color 객체로 변환한다.
	 *
	 * @param	r	빨강 (0~255)
	 * @param	g	초록 (0~255)
	 * @param	b	파랑 (0~255)
	 * @param	a	알파 (0.0~1.0)
	 * @return	변환된 색 객체
	 */
	inline fun rgb(r: Int, g: Int, b: Int, a: Float = 1.0f): Color {
		if(r > 255 || g > 255 || b > 255 || r < 0 || g < 0 || b < 0 || a > 1f || a < 0f)
			throw IllegalArgumentException("invalid color value");
		return Color(r / 255f, g / 255f, b / 255f, a);
	}
	
	/**
	 * 지정한 시간 후 특정 서브루틴을 한 번만 실행한다.
	 * 
	 * @param delay		지연 시간(초)
	 * @param operation	실행할 서브루틴
	 */
	fun setTimeout(delay: Float, operation: () -> Unit): Timer {
		var timer: Timer? = null;  // 선언 이후 대입해야 해서 어쩔 수 없이 var
		timer = Timer(delay) {
			operation();
			timer?.unregister();
		}.register();
		return timer!!;
	}
}
