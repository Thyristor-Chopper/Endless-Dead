package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import java.util.WeakHashMap;

import kotlin.properties.Delegates;

/**
 * 점수를 관리하는 싱글톤
 *
 * libGDX는 Game()의 인스턴스가 두 개 이상일 이유가 없고 창을 두 개 이상 띄우지 못하므로
 * 큰 위험성은 없다.
 */
object ScoreManager {
	/**
	 * 현재 점수
	 */
	@JvmStatic var score: Int = 0
		private set(value) {
			if(value < 0) field = 0;
			else field = value;
		};

	/**
	 * 점수를 준다.
	 *
	 * @param amount	줄 점수
	 */
	@JvmStatic fun addScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score += amount;
	}

	/**
	 * 점수를 감점한다.
	 *
	 * @param amount	차감할 점수
	 */
	@JvmStatic fun subtractScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score -= amount;
	}

	/**
	 * 점수를 초기화한다.
	 */
	@JvmStatic fun resetScore() {
		score = 0;
	}
}
