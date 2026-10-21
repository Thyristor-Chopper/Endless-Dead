package io.potatogun.endlessdead.world;

/**
 * 특정한 랜덤 시드로 동작하는 월드
 */
interface SeededWorld {
	/**
	 * 이 월드의 시드
	 */
	val seed: Long;
}
