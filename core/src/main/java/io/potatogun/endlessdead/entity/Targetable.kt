package io.potatogun.endlessdead.entity;

/**
 * 타겟을 정하여 공격하는 개체
 */
interface Targetable {
	/**
	 * 공격 대상
	 */
	val target: LivingEntity?;
	/**
	 * 개체 감지 범위 (0: 제한 없음)
	 */
	val followRange: Float;
}
