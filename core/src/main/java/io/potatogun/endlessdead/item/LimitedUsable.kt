package io.potatogun.endlessdead.item;

/**
 * 사용 횟수가 제한된 아이템
 */
interface LimitedUsable : Usable {
	/**
	 * 최대 사용 횟수
	 */
	val maxUses: Int;
	/**
	 * 남은 사용 횟수
	 */
	val remainingUses: Int;
}
