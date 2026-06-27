package io.potatogun.endlessdead.entity;

/**
 * 다른 개체를 공격하는 개체
 */
interface AttackListener {
	/**
	 * 개체가 다른 누군가를 공격했을 때 콜백 함수
	 *
	 * @param victim 공격 대상
	 */
	fun onAttack(victim: LivingEntity) {}

	/**
	 * 개체가 다른 누군가를 처치했을 때 콜백 함수
	 *
	 * @param victim 공격 대상
	 */
	fun onKill(victim: LivingEntity) {}
}
