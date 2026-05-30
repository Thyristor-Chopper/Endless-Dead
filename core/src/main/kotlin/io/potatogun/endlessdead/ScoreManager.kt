package io.potatogun.endlessdead;

/**
 * 게임의 점수를 관리하는 싱글톤
 */
object ScoreManager {
	/**
	 * 현재 점수
	 */
	var score: Int = 0
		private set(value) {
			if(value < 0) field = 0;
			else field = value;
		};

	/**
	 * 점수를 준다.
	 *
	 * @param amount	줄 점수
	 */
	fun addScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score += amount;
	}

	/**
	 * 점수를 감점한다.
	 *
	 * @param amount	차감할 점수
	 */
	fun subtractScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score -= amount;
	}

	/**
	 * 점수를 초기화한다.
	 */
	fun resetScore() {
		score = 0;
	}
}
