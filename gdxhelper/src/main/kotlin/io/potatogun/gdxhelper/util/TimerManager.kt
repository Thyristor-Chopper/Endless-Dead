package io.potatogun.gdxhelper.util;

/**
 * 타이머 객체들을 관리한다.
 */
class TimerManager {
	private val timers = mutableListOf<Timer>();

	/**
	 * 타이머 등록
	 *
	 * @param timer 등록할 타이머
	 */
	fun registerTimer(timer: Timer) {
		timers.add(timer);
	}

	/**
	 * 타이머 등록 해제
	 *
	 * @param timer 제거할 타이머
	 * @return      성공 여부
	 */
	fun unregisterTimer(timer: Timer): Boolean = timers.remove(timer);

	/**
	 * 타이머를 갱신한다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	fun tick(delta: Float) {
		timers.toList().forEach {
			it.tick(delta);
			if(it !is RepeatingTimer && it.executed)
				timers.remove(it);
		};
	}

	/**
	 * 등록된 모든 타이머를 해제한다.
	 */
	fun clearTimers() {
		timers.clear();
		// 자바 가상머신이 gc 해주길...
		//   참 소멸자 개념이랑 C++처럼 수동으로 delete하는 게 왜 없냐...
	}
}
