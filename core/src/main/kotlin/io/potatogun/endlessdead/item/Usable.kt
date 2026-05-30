package com.oop.game.item;

/**
 * '사용할 수 있다'의 개념이 있는 아이템
 */
interface Usable {
	/**
	 * 마우스를 꾹 누를 때 연속으로 발동 가능한지의 여부
	 */
	val allowContinuousUse: Boolean;
	
	/**
	 * 아이템 사용 처리
	 *
	 * @return 사용 성공 여부
	 */
	fun use(): Boolean;
}
