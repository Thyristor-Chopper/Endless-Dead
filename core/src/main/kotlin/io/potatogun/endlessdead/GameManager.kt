package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

/**
 * 게임의 상태를 관리하는 싱글톤
 */
object GameManager {
	/**
	 * 게임의 현재 상태
	 */
	private var state = GameState.STANDBY;
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
	 * 준비 상태로 전환한다.
	 */
	fun standBy() {
		state = GameState.STANDBY;
		Gdx.graphics.setForegroundFPS(20);  // 20fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
	}

	/**
	 * 게임 진행 상태로 전환한다.
	 */
	fun setPlaying() {
		Gdx.graphics.setForegroundFPS(Constants.FPS);
		state = GameState.PLAYING;
	}

	/**
	 * 게임을 종료 상태로 전환한다.
	 */
	fun setGameOver() {
		state = GameState.GAME_OVER;
		Gdx.graphics.setForegroundFPS(20);  // 20fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
	}

	/**
	 * 게임을 일시 중지한다.
	 */
	fun pause() {
		state = GameState.PAUSED;
		Gdx.graphics.setForegroundFPS(20);  // 20fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
	}

	/**
	 * 일시 중지된 게임을 계속한다.
	 */
	inline fun resume() {
		setPlaying();
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
