package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import java.util.WeakHashMap;

import kotlin.properties.Delegates;

/**
 * 점수를 관리하는 클래스
 *
 * 원래는 object 싱글톤이었으나 게임 인스턴스 하나마다 점수 관리자가 각각 있는 게
 *   더 적절할 것 같아 변경했다. (싱글톤 안 쓰고 이렇게 한 이유가 있으므로 참고)
 *
 * 설득: 만약 Game(EndlessDead) 인스턴스를 두 개 만들고 각각 두 개의 창으로 띄웠는데
 *   object 싱글톤으로 하면 두 분리된 게임이 같은 점수를 공유하게 되는 비정상적인 상황이 발생한다.
 *
 * 하지만 한 게임 인스턴스 당 ScoreManager 하나라는 안전 장치는 유지하기 위해
 *   WorldViewer나 GameManager와 같은 방식으로 한 번만 생성할 수 있게 하였다.
 *
 * @param game 게임 인스턴스
 */
class ScoreManager(game: EndlessDead) {
	/**
	 * 현재 점수
	 */
	var score: Int = 0
		private set(value) {
			if(value < 0) field = 0;
			else field = value;
		};

	init {
		// 같은 게임 인스턴스에 대해 두 개 이상의 ScoreManager를 만들지 못하게 한다.
		if(ScoreManager.managerInstance[game] != null)
			throw IllegalStateException("only one instance of ScoreManager may be created for each game instances");
		ScoreManager.managerInstance[game] = this;
	}

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

	companion object {
		private val managerInstance = WeakHashMap<EndlessDead, ScoreManager>();
	}
}
