package io.potatogun.gdxhelper.timer;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import java.util.function.BooleanSupplier;

/**
 * 게임 프레임과는 독립적인 타이머를 실행할 수 있는 함수들을 모은 곳이다.
 */
object Tasks {
	/**
	 * 지정한 시간 후 특정 서브루틴을 한 번만 실행한다. (코틀린이라면 반드시 이쪽을 호출하여 인라인 최적화의 맛을 보자.)
	 *
	 * 우리가 만든 Timer 클래스는 우리 게임의 상태 등에 종속적이지만 이쪽은 게임과는 독립적이기 때문에 libGDX의 Timer 클래스를 직접 사용한다.
	 * 
	 * @param delay     지연 시간(초)
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmSynthetic inline fun setTimeout(delay: Float, crossinline operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				operation();
			}
		}.also { Timer.schedule(it, delay) };
	}

	/**
	 * 지정한 시간 후 지정한 조건을 만족하면 특정 서브루틴을 한 번만 실행한다. (코틀린이라면 반드시 이쪽을 호출하여 인라인 최적화의 맛을 보자.)
	 *
	 * @param delay     지연 시간(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmSynthetic inline fun setTimeout(delay: Float, crossinline condition: () -> Boolean, crossinline operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				if(condition())
					operation();
			}
		}.also { Timer.schedule(it, delay) };
	}

	/**
	 * 지정한 시간 후 특정 서브루틴을 한 번만 실행한다 (자바에서 사용).
	 *
	 * @param delay     지연 시간(초)
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmStatic fun setTimeout(delay: Float, operation: Runnable): Task = setTimeout(delay, operation::run);

	/**
	 * 지정한 시간 후 지정한 조건을 만족하면 특정 서브루틴을 한 번만 실행한다 (자바에서 사용).
	 *
	 * @param delay     지연 시간(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmStatic fun setTimeout(delay: Float, condition: BooleanSupplier, operation: Runnable): Task = setTimeout(delay, condition::getAsBoolean, operation::run);

	/**
	 * 지정한 시간마다 특정 서브루틴을 실행한다. (코틀린이라면 반드시 이쪽을 호출하여 인라인 최적화의 맛을 보자.)
	 *
	 * 우리가 만든 Timer 클래스는 우리 게임의 상태 등에 종속적이지만 이쪽은 게임과는 독립적이기 때문에 libGDX의 Timer 클래스를 직접 사용한다.
	 * 
	 * @param interval  실행 간격(초)
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmSynthetic inline fun setInterval(interval: Float, crossinline operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				operation();
			}
		}.also { Timer.schedule(it, interval, interval) };
	}

	/**
	 * 지정한 시간마다 특정 서브루틴을 지정한 조건을 만족하면 실행한다. (코틀린이라면 반드시 이쪽을 호출하여 인라인 최적화의 맛을 보자.)
	 *
	 * @param interval  실행 간격(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmSynthetic inline fun setInterval(interval: Float, crossinline condition: () -> Boolean, crossinline operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				if(condition())
					operation();
			}
		}.also { Timer.schedule(it, interval, interval) };
	}

	/**
	 * 지정한 시간마다 특정 서브루틴을 실행한다 (자바에서 사용).
	 *
	 * @param interval  실행 간격(초)
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmStatic fun setInterval(interval: Float, operation: Runnable): Task = setInterval(interval, operation::run);

	/**
	 * 지정한 시간마다 특정 서브루틴을 지정한 조건을 만족하면 실행한다 (자바에서 사용).
	 *
	 * @param interval  실행 간격(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmStatic fun setInterval(interval: Float, condition: BooleanSupplier, operation: Runnable): Task = setInterval(interval, condition::getAsBoolean, operation::run);
}
