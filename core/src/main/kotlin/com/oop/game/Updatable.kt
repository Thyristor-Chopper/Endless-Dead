package com.oop.game;

/**
 * 매 프레임마다 상태 갱신이 가능한 오브젝트
 */
interface Updatable {
	/**
	 * TimeStopper 아이템에 영향을 받는지의 여부
	 */
	val canUpdateWhileFrozen: Boolean;
	
	fun update(delta: Float) {}
}
