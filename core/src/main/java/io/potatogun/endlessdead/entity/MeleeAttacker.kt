package io.potatogun.endlessdead.entity;

/**
 * 근접 공격자
 */
interface MeleeAttacker {
	/**
	 * 근접 공격 피해량
	 */
	val attackDamage: Int;
	/**
	 * 공격 간격 (낮을수록 빠름)
	 */
	val attackInterval: Float;
}
