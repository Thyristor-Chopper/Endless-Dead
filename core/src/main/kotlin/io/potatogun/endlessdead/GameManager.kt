package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

object GameManager {
	private var state = GameState.TITLE;
	val isPlaying: Boolean
		get() = (state == GameState.PLAYING);
	val isGameOver: Boolean
		get() = (state == GameState.GAME_OVER);
	val isPaused: Boolean
		get() = (state == GameState.PAUSED);
	
	fun setPlaying() {
		Gdx.graphics.setForegroundFPS(Constants.FPS);
		state = GameState.PLAYING;
	}
	
	fun setGameOver() {
		state = GameState.GAME_OVER;
		Gdx.graphics.setForegroundFPS(10);  // 10fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
	}
	
	fun pause() {
		state = GameState.PAUSED;
		Gdx.graphics.setForegroundFPS(10);  // 10fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
	}
	
	inline fun resume() {
		setPlaying();
	}
	
	/**
	 * 게임의 현재 상태를 나타내는 열거형.
	 */
	private enum class GameState {
		TITLE,
		PLAYING,
		PAUSED,
		GAME_OVER;
	}
}
