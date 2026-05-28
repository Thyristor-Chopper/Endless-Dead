package com.oop.game;

import com.badlogic.gdx.Screen;

/**
 * 일정 시간마다 특정 작업을 실행하게 해 주는 클래스
 *
 * @param interval		실행 간격(초)
 * @param onlyInPlay	게임 오버가 아닐 때만 실행할지의 여부
 * @param operation		실행할 서브루틴
 */
class Timer(val interval: Int, val onlyInPlay: Boolean = true, private val operation: () -> Unit) {
	private var delta = interval - 1
		set(value) {
			if(value < 0) field = 0;
			else if(value >= interval) field = interval - 1;
			else field = value;
		};
	
	/**
	 * 대기시간 감소 및 때가 되면 실행
	 */
	internal fun tick() {
		if(delta == 0) {
			operation();
			reset();
		} else {
			delta--;
		}
	}
	
	/**
	 * 대기시간 초기화
	 */
	fun reset() {
		delta = interval - 1;
	}
}
