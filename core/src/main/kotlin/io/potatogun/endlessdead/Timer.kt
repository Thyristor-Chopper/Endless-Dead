package io.potatogun.endlessdead;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param game			타이머가 속한 게임 인스턴스 (onlyInPlay가 true이면 null이면 안 된다.)
 * @param interval		실행 간격(초)
 * @param onlyInPlay	게임 오버가 아닐 때만 실행할지의 여부
 * @param operation		실행할 서브루틴
 */
class Timer(val interval: Float, private val operation: () -> Unit) {
	private var timer = interval
		set(value) {
			if(value < 0f) field = 0f;
			else if(value > interval) field = interval;
			else field = value;
		};

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
