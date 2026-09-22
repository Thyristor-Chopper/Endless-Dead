package io.potatogun.endlessdead.item;

/**
 * 사용 쿨타임이 있는 아이템
 */
interface Cooldownable : Usable {
	/**
	 * 사용 쿨타임
	 */
	val interval: Float;
	/**
	 * 쿨타임 초기화까지 남은 시간
	 */
	val remainingCooldown: Float;
}
