package io.potatogun.endlessdead;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer as GdxTimer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param game			타이머가 속한 게임 인스턴스 (onlyInPlay가 true이면 null이면 안 된다.)
 * @param interval		실행 간격(초)
 * @param onlyInPlay	게임 오버가 아닐 때만 실행할지의 여부
 * @param operation		실행할 서브루틴
 */
class Timer(private val game: EndlessDead? = null, val interval: Float, private val delay: Float = interval, private val onlyInPlay: Boolean = true, private val operation: () -> Unit) {
	private val task = object : Task() {
		override fun run() {
			if(!onlyInPlay || (onlyInPlay && game!!.gameManager.isPlaying))  // onlyInPlay가 true면 game이 null이 되지 못하게 이미 생성자에서 처리함
				operation();
		}
	};

	constructor(interval: Float, operation: () -> Unit) : this(null, interval, interval, false, operation);

	constructor(interval: Float, delay: Float, operation: () -> Unit) : this(null, interval, delay, false, operation);

	constructor(game: EndlessDead? = null, interval: Float, onlyInPlay: Boolean = true, operation: () -> Unit) : this(game, interval, interval, onlyInPlay, operation);

	init {
		if(onlyInPlay && game == null)
			throw IllegalArgumentException("game must not be null if onlyInPlay is true");
	}

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
