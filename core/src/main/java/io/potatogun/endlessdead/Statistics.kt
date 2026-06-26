package io.potatogun.endlessdead;

/**
 * 게임 통계
 */
object Statistics {
	@JvmStatic var survivedDuration = 0
		internal set;
	@JvmStatic var openedContainerCount = 0
		internal set;
	@JvmStatic var killedZombieCount = 0
		internal set;
	@JvmStatic var fireCount = 0
		internal set;
	@JvmStatic var totalDamage = 0
		internal set;
}
