package com.oop.game;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param timerInterval	실행 간격
 * @param onlyInPlay	게임 오버가 아닐 때만 실행할지의 여부
 * @param run			실행할 서브루틴
 */
class Timer(timerInterval: Int, val onlyInPlay: Boolean = true, internal val run: () -> Unit) {
	/**
	 * 몇 초마다 실행할지의 간격(초)
	 */
	val interval = timerInterval - 1;
	private var delta = interval
		set(value) {
			if(value < 0) field = 0;
			else if(value > interval) field = interval;
			else field = value;
		};
	/**
	 * 현재 타이머가 실행되어야 하는지 여부
	 */
	val canRun: Boolean
		get() = (delta == 0);
	
	/**
	 * 대기시간 감소
	 */
	internal fun tick() {
		delta--;
	}
	
	/**
	 * 대기시간 초기화
	 */
	internal fun reset() {
		delta = interval;
	}
}
