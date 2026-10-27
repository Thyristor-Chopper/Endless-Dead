package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.util.max2;

/**
 * 점수 관리자
 */
class ScoreManager {
	/**
	 * 현재 점수
	 */
	var score: Int = 0
		private set;

	/**
	 * 점수를 준다.
	 *
	 * @param amount 줄 점수
	 */
	fun addScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score += amount;
	}

	/**
	 * 점수를 감점한다.
	 *
	 * @param amount 차감할 점수
	 */
	fun subtractScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score = max2(0, score - amount);
	}

	/**
	 * 점수를 초기화한다.
	 */
	fun resetScore() {
		score = 0;
	}

	inline fun copy(destination: ScoreManager) {
		destination.addScore(score);
	}
}
