package io.potatogun.endlessdead;

/**
 * 게임 통계
 */
object Statistics {
	@JvmStatic var survivedDuration = 0
		@JvmSynthetic internal set;
	@JvmStatic var openedContainerCount = 0
		@JvmSynthetic internal set;
	@JvmStatic var killedZombieCount = 0
		@JvmSynthetic internal set;
	@JvmStatic var fireCount = 0
		@JvmSynthetic internal set;
	@JvmStatic var totalDamage = 0
		@JvmSynthetic internal set;
}
