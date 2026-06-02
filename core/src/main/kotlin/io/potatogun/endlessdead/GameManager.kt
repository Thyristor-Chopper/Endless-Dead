package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import kotlin.properties.Delegates;

/**
 * 게임의 상태를 관리하는 싱글톤
 */
object GameManager {
	/**
	 * 게임의 현재 상태
	 */
	private var state: GameState by Delegates.observable(GameState.STANDBY) { _, _, new -> 
		if(new == GameState.PLAYING)
			Gdx.graphics.setForegroundFPS(Constants.FPS);
		else
			Gdx.graphics.setForegroundFPS(Constants.PASSIVE_FPS);  // 20fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
	};
	/**
	 * 현재 라운드 (0이면 아직 게임이 시작되지 않은 것)
	 */
	var round: Int by Delegates.observable(0) { _, _, new -> Window.titleBarInfo = (if(new > 0) "Round $new" else null) }
		private set;
	/**
	 * 현재 점수
	 */
	var score: Int = 0
		private set(value) {
			if(value < 0) field = 0;
			else field = value;
		};
	/**
	 * 현재 게임이 진행 중인지의 여부
	 */
	val isPlaying: Boolean
		get() = (state == GameState.PLAYING);
	/**
	 * 현재 게임이 끝났는지의 여부
	 */
	val isGameOver: Boolean
		get() = (state == GameState.GAME_OVER);
	/**
	 * 현재 게임이 일지 중지된 상태인지의 여부
	 */
	val isPaused: Boolean
		get() = (state == GameState.PAUSED);

	/**
	 * 준비 상태(타이틀 화면)로 전환한다.
	 */
	fun standBy() {
		round = 0;
		Window.titleBarStats = null;
		state = GameState.STANDBY;
	}

	/**
	 * 게임 진행 상태로 전환한다.
	 */
	fun setPlaying() {
		if(state != GameState.PAUSED) round++;
		state = GameState.PLAYING;
	}

	/**
	 * 게임을 종료 상태로 전환한다.
	 */
	fun setGameOver() {
		Window.titleBarStats = null;
		state = GameState.GAME_OVER;
	}

	/**
	 * 게임을 일시 중지한다.
	 */
	fun pause() {
		state = GameState.PAUSED;
	}

	/**
	 * 일시 중지된 게임을 계속한다.
	 */
	inline fun resume() {
		setPlaying();
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

	/**
	 * 게임의 현재 상태를 나타내는 열거형.
	 */
	private enum class GameState {
		STANDBY,
		PLAYING,
		PAUSED,
		GAME_OVER;
	}
}
