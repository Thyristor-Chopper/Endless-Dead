package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.entity.LivingEntity;

interface MeleeAttackable {
	/**
	 * 손공격 피해량
	 */
	val attackDamage: Int;
	/**
	 * 손공격 간격 (낮을수록 빠름)
	 */
	val attackInterval: Float;

	/**
	 * 공격 대상에게 대미지를 입힌다.
	 *
	 * @param target 대상
	 * @return 성공 여부
	 */
	fun damageTarget(target: LivingEntity): Boolean;

	/**
	 * 주변 개체를 손공격한다.
	 */
	fun meleeAttackNearby();
}
