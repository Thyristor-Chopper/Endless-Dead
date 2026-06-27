package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * 내가 공격당한 걸 감지할 수 있는 개체
 */
interface DamageListener {
	/**
	 * 대미지를 받았을 때 실행할 콜백 함수
	 *
	 * @param damage   받은 피해량
	 * @param attacker 공격자
	 */
	fun onDamage(damage: Int, attacker: Entity?) {}

	/**
	 * 죽었을 때 실행할 콜백 함수
	 *
	 * @param killer 공격자
	 */
	fun onDeath(killer: Entity?) {}
}
