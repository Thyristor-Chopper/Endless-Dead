package io.potatogun.gdxhelper.util;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @property interval  실행 간격(초)
 * @property operation 실행할 서브루틴
 */
class Timer(val interval: Float, private val operation: () -> Unit) {
	private var timer = interval
		set(value) {
			if(value < 0f) field = 0f;
			else if(value > interval) field = interval;
			else field = value;
		};

	/**
	 * 타이머를 갱신한다.
     *
     * @param delta 직전 프레임과의 시간 간격(초)
	 */
	internal fun tick(delta: Float) {
		timer -= delta;
		if(timer == 0f) {
			timer = interval;
			operation();
		}
	}

	/**
	 * 대기시간을 초기화한다.
	 */
	fun reset() {
		timer = interval;
	}
}
