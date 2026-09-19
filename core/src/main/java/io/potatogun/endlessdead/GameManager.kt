package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.endlessdead.world.ZombieWorld;

import kotlin.properties.Delegates;

/**
 * 게임의 상태를 관리하는 싱글톤
 *
 * libGDX는 Game()의 인스턴스가 두 개 이상일 이유가 없고 창을 두 개 이상 띄우지 못하므로
 * 큰 위험성은 없다.
 */
object GameManager {
	/**
	 * 게임 인스턴스
	 */
	private lateinit var game: EndlessDead;  // 초기화 후 바뀔 일 없음 코틀린이 원래 나 같은 개발자를 열받게 하는 언어라...
	/**
	 * 점수 관리자
	 */
	@JvmStatic val scoreManager = ScoreManager();
	/**
	 * 통계 관리자
	 */
	@JvmStatic val statistics = StatisticsManager();
	/**
	 * 게임 진행 시간 (진행 중에만)
	 */
	@JvmStatic var gameTime = 0f
		private set;
	/**
	 * 월드, 개체 등에 종속되지 않는 전역 타이머 관리자
	 */
	@JvmStatic val globalTimers = TimerManager();
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
	@JvmStatic var round: Int by Delegates.observable(0) { _, _, new -> Window.titleBarInfo = (if(new > 0) "Round $new" else null) }
		private set;
	/**
	 * 현재 게임이 진행 중인지의 여부
	 */
	@JvmStatic val isPlaying: Boolean
		get() = (state == GameState.PLAYING);
	/**
	 * 현재 게임이 끝났는지의 여부
	 */
	@JvmStatic val isGameOver: Boolean
		get() = (state == GameState.GAME_OVER);
	/**
	 * 현재 게임이 일시 중지된 상태인지의 여부
	 */
	@JvmStatic val isPaused: Boolean
		get() = (state == GameState.PAUSED);

	/**
	 * 게임 관리자를 초기화한다.
	 *
	 * @param game 게임 인스턴스
	 */
	@JvmSynthetic internal fun init(game: EndlessDead) {
		if(::game.isInitialized)
			throw IllegalStateException("game manager is already initialised");
		this.game = game;
	}

	/**
	 * 준비 상태(타이틀 화면)로 전환한다.
	 */
	@JvmStatic fun standBy() {
		if(!::game.isInitialized)
			throw IllegalStateException("game manager is not initialised");

		// 월드를 메모리에서 내리기
		game.worldProjector.unloadWorld(true);

		// 통계, 시간 및 라운드 초기화
		resetAll();
		round = 0;
		Window.titleBarStats = null;
		gameTime = 0f;

		// 상태 전환
		state = GameState.STANDBY;

		// 타이틀 화면으로 전환
		game.setScreen(game.titleScreen);
	}

	/**
	 * 새 게임을 시작한다.
	 */
	@JvmStatic fun newGame() {
		if(!::game.isInitialized)
			throw IllegalStateException("game manager is not initialised");

		// 통계 및 시간 초기화
		resetAll();
		round++;
		gameTime = 0f;

		// 상태 전환
		state = GameState.PLAYING;

		// 월드 생성 후 월드 표시기 화면으로 전환
		game.worldProjector.loadWorld(ZombieWorld(), disposePreviousWorld = true);
		if(game.getScreen() !== game.worldProjector)
			game.setScreen(game.worldProjector);
	}

	/**
	 * 게임을 종료 상태로 전환한다.
	 */
	@JvmStatic fun setGameOver() {
		if(!::game.isInitialized)
			throw IllegalStateException("game manager is not initialised");
		Window.titleBarStats = null;
		state = GameState.GAME_OVER;
	}

	/**
	 * 게임을 일시 중지한다.
	 */
	@JvmStatic fun pause() {
		if(!::game.isInitialized)
			throw IllegalStateException("game manager is not initialised");

		state = GameState.PAUSED;
	}

	/**
	 * 일시 중지된 게임을 계속한다.
	 */
	@JvmStatic fun resume() {
		if(!::game.isInitialized)
			throw IllegalStateException("game manager is not initialised");
		if(state != GameState.PAUSED)
			throw IllegalStateException("game is not paused");

		state = GameState.PLAYING;
	}

	/**
	 * 점수 및 통계 초기화
	 */
	@JvmStatic fun resetAll() {
		scoreManager.resetScore();
		statistics.survivedDuration = 0;
		statistics.openedContainerCount = 0;
		statistics.killedZombieCount = 0;
		statistics.fireCount = 0;
		statistics.totalDamage = 0;
	}

	/**
	 * 게임 진행 시간을 증가한다.
	 * 
	 * @param delta 직전 프레임과의 간격(초)
	 */
	@JvmSynthetic internal fun tickGameTime(delta: Float) {
		if(state != GameState.PLAYING) return;
		gameTime += delta;
	}

	/**
	 * 전역 타이머 관리자를 갱신한다.
	 * 
	 * @param delta 직전 프레임과의 간격(초)
	 */
	@JvmSynthetic internal fun tickGlobalTimers(delta: Float) {
		globalTimers.tick(delta);
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

	/**
	 * 점수 관리자
	 */
	class ScoreManager internal constructor() {
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
			score -= amount;
		}

		/**
		 * 점수를 초기화한다.
		 */
		fun resetScore() {
			score = 0;
		}
	}

	/**
	 * 게임 통계 관리자
	 */
	class StatisticsManager internal constructor() {
		var survivedDuration = 0
			@JvmSynthetic internal set;
		var openedContainerCount = 0
			@JvmSynthetic internal set;
		var killedZombieCount = 0
			@JvmSynthetic internal set;
		var fireCount = 0
			@JvmSynthetic internal set;
		var totalDamage = 0
			@JvmSynthetic internal set;
	}
}
