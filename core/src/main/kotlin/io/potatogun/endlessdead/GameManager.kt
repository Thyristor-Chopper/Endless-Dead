package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import java.util.WeakHashMap;

import kotlin.properties.Delegates;

/**
 * 게임의 상태를 관리하는 클래스
 *
 * 원래는 object 싱글톤이었으나 게임 인스턴스 하나마다 게임 상태가 각각 있는 게
 *   더 적절할 것 같아 변경했다. (싱글톤 안 쓰고 이렇게 한 이유가 있으므로 참고)
 *
 * 하지만 한 게임 인스턴스 당 GameManager 하나라는 안전 장치는 유지하기 위해
 *   WorldViewer와 같은 방식으로 한 번만 생성할 수 있게 하였다.
 *
 * @param game 게임 인스턴스
 */
class GameManager(game: EndlessDead) {
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

	init {
		// 같은 게임 인스턴스에 대해 두 개 이상의 GameManager를 만들지 못하게 한다.
		if(GameManager.managerInstance[game] != null)
			throw IllegalStateException("only one instance of GameManager may be created for each game instances");
		GameManager.managerInstance[game] = this;
	}

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
	 * 게임의 현재 상태를 나타내는 열거형.
	 */
	private enum class GameState {
		STANDBY,
		PLAYING,
		PAUSED,
		GAME_OVER;
	}

	companion object {
		private val managerInstance = WeakHashMap<EndlessDead, GameManager>();
	}
}
