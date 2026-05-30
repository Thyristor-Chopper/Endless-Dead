package io.potatogun.endlessdead;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer as GdxTimer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param interval		실행 간격(초)
 * @param onlyInPlay	게임 오버가 아닐 때만 실행할지의 여부
 * @param operation		실행할 서브루틴
 */
class Timer(val interval: Float, val delay: Float = interval, val onlyInPlay: Boolean = true, private val operation: () -> Unit) {
	private var delta = interval - 1
		set(value) {
			if(value < 0f) field = 0f;
			else if(value >= interval) field = interval - 1f;
			else field = value;
		};
	private val task = object : Task() {
		override fun run() {
			if(!onlyInPlay || (onlyInPlay && GameManager.isPlaying))
				operation();
		}
	};

	constructor(interval: Float, onlyInPlay: Boolean = true, operation: () -> Unit) : this(interval, interval, onlyInPlay, operation);

	/**
	 * 타이머를 등록한다.
	 */
	fun register(): Timer {
		GdxTimer.schedule(task, delay, interval);
		return this;
	}

	/**
	 * 타이머 등록을 해제한다.
	 */
	fun unregister() {
		task.cancel();
	}

	/**
	 * 대기시간을 초기화한다.
	 */
	fun reset() {
		unregister();
		register();
	}
}
