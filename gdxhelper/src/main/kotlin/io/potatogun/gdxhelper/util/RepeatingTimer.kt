package io.potatogun.gdxhelper.util;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param interval  실행 간격(초)
 * @param operation 실행할 서브루틴
 */
class RepeatingTimer(interval: Float, operation: () -> Unit) : Timer(interval, operation) {
	override fun tick(delta: Float) {
		super.tick(delta);
		if(executed) reset();
	}
}
