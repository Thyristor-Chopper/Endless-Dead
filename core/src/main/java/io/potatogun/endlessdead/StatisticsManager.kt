package io.potatogun.endlessdead;

/**
 * 게임 통계 관리자
 */
class StatisticsManager {
	@JvmField var survivedDuration = 0;
	@JvmField var openedContainerCount = 0;
	@JvmField var killedZombieCount = 0;
	@JvmField var fireCount = 0;
	@JvmField var totalDamage = 0;
}
