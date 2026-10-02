package io.potatogun.endlessdead.world;

/**
 * 시간 제한 월드
 */
interface TimeLimitedWorld {
	/**
	 * 이 월드의 제한 시간(초)
	 */
	val timeLimit: Float;
	/**
	 * 남은 시간(초)
	 */
	val remainingTime: Float;
}
