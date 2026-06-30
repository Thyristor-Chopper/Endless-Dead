package io.potatogun.gdxhelper.util;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @constructor 코틀린용 생성자
 * @param interval  실행 간격(초)
 * @param condition 실행 조건
 * @param operation 실행할 서브루틴
 */
class RepeatingTimer(interval: Float, condition: (() -> Boolean)? = null, operation: (Timer) -> Unit) : Timer(interval, condition, operation) {
	/**
	 * 반복 타이머를 생성한다 (자바에서 사용).
	 *
	 * @constructor 자바용 생성자
	 * @param interval  실행 간격(초)
	 * @param operation 실행할 서브루틴
	 */
	constructor(interval: Float, operation: Consumer<Timer>) : this(interval, null, operation::accept);

	/**
	 * 조건을 가진 반복 타이머를 생성한다 (자바에서 사용).
	 *
	 * @constructor 조건을 가진 자바용 생성자
	 * @param interval  실행 간격(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 */
	constructor(interval: Float, condition: BooleanSupplier, operation: Consumer<Timer>) : this(interval, condition::getAsBoolean, operation::accept);

	override fun tick(delta: Float) {
		super.tick(delta);
		if(executed) reset();
	}
}
