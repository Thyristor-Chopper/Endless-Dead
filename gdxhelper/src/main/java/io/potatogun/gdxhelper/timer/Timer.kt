package io.potatogun.gdxhelper.timer;

import java.util.function.BooleanSupplier;

/**
 * 지정된 시간 후 특정 작업을 실행하게 해 주는 클래스
 *
 * @constructor 코틀린용 생성자
 * @property delay     대기 시간(초)
 * @property condition 실행 조건
 * @property operation 실행할 서브루틴
 */
open class Timer @JvmOverloads constructor(private val delay: Float, internal val condition: BooleanSupplier? = null, private val operation: Runnable) {
	private var timer = delay
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	/**
	 * 타이머가 실행되었는지의 여부
	 */
	var executed = false
		private set;

	/**
	 * 타이머를 갱신한다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	internal open fun tick(delta: Float) {
		timer -= delta;
		if(timer == 0f) {
			operation.run();
			executed = true;
		}
	}

	/**
	 * 대기 시간을 초기화한다.
	 */
	fun reset() {
		timer = delay;
		executed = false;
	}
}
