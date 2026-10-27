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

	inline fun copy(destination: StatisticsManager) {
		destination.let {
			it.survivedDuration = survivedDuration;
			it.openedContainerCount = openedContainerCount;
			it.killedZombieCount = killedZombieCount;
			it.fireCount = fireCount;
			it.totalDamage = totalDamage;
		};
	}
}
