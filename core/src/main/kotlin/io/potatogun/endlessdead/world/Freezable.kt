package io.potatogun.endlessdead.world;

/**
 * 시간 멈춤이 가능한 월드
 */
interface Freezable {
	/**
	 * 현재 시간이 멈춰있는지 여부
	 */
	val isFrozen: Boolean;

	/**
	 * 시간을 멈춘다.
	 *
	 * @param duration 멈춤 지속 시간(초)
	 */
	fun freeze(duration: Float);

	/**
	 * 시간을 다시 흐르게 한다.
	 */
	fun unfreeze();
}
