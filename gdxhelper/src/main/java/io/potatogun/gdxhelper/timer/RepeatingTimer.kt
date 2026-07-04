package io.potatogun.gdxhelper.timer;

import java.util.function.BooleanSupplier;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param interval  실행 간격(초)
 * @param condition 실행 조건
 * @param operation 실행할 서브루틴
 */
class RepeatingTimer @JvmOverloads constructor(interval: Float, condition: BooleanSupplier? = null, operation: Runnable) : Timer(interval, condition, operation) {
	override fun tick(delta: Float) {
		super.tick(delta);
		if(executed) reset();
	}
}
