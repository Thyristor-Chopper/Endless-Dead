package com.oop.game;

/**
 * 매 프레임마다 상태 갱신이 가능한 오브젝트
 */
interface Updatable {
	fun update(delta: Float) {}
}
