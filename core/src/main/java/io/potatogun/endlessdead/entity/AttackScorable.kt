package io.potatogun.endlessdead.entity;

/**
 * 공격할 때 점수를 주는 개체
 */
interface AttackScorable {
	/**
	 * 한 번 공격 당 점수
	 */
	val attackScore: Int;
}
