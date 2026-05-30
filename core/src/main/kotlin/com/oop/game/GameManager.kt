package com.oop.game;

import com.badlogic.gdx.Gdx;

object GameManager {
	var state = GameState.TITLE
		private set;
	
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
}
