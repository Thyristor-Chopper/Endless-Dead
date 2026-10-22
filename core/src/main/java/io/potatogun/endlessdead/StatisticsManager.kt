package io.potatogun.endlessdead;

/**
 * 게임 통계 관리자
 */
class StatisticsManager {
	var survivedDuration = 0
		@JvmSynthetic internal set;
	var openedContainerCount = 0
		@JvmSynthetic internal set;
	var killedZombieCount = 0
		@JvmSynthetic internal set;
	var fireCount = 0
		@JvmSynthetic internal set;
	var totalDamage = 0
		@JvmSynthetic internal set;
}
