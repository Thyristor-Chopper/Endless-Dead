package io.potatogun.endlessdead.entity;

/**
 * 자동 공격 대상을 정할 수 있는 개체
 */
interface AttackTargetable {
	/**
	 * 공격 대상
	 */
	var target: LivingEntity?;
}
