package com.oop.game;

abstract class LivingGameObject(x: Float, y: Float, width: Float, height: Float) : GameObject(x, y, width, height) {
	/**
	 * 이 객체가 아직 '살아있는지' 여부.
	 *
	 * GameWorld 가 매 프레임 removeDead() 를 호출하면,
	 *   이 값이 false 인 객체가 월드에서 정리된다.
	 *
	 * 기본값은 true — 대부분의 객체는 '살아있는 게 기본' 이기 때문.
	 * 'open' 이므로 서브클래스에서 원한다면 override 할 수 있다.
	 *   예) class Bullet(val worldHeight: Float) {
	 *           override fun isAlive() = y in 0f..worldHeight   // 화면 안에 있을 때만 살아있음
	 *       }
	 */
	open fun isAlive(): Boolean = true;
}
