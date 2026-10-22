package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.ArraySuppliers;
import io.potatogun.gdxhelper.util.Updatable;
import io.potatogun.endlessdead.entity.teleportToCenter;
import io.potatogun.endlessdead.world.ZombieWorld;

/**
 * 게임 상태 관리자
 *
 * @property game 게임 인스턴스
 */
class GameManager(private val game: EndlessDead) : Updatable {
	/**
	 * 점수 관리자
	 */
	@JvmField val scoreManager = ScoreManager();  // 디컴파일해서 확인한 결과 싱글톤에서 @JvmField를 하면 GameManager.INSTANCE.scoreManager가 아닌 GameManager.scoreManager로 바로 접근 가능(효과도 같이 있는 것)
	/**
	 * 통계 관리자
	 */
	@JvmField val statistics = StatisticsManager();
	/**
	 * 월드, 개체 등에 종속되지 않는 전역 타이머 관리자
	 */
	@JvmField val globalTimers = TimerManager();
	/**
	 * 라운드 숨김 여부
	 */
	private var hideRound = java.lang.Boolean.getBoolean("zombie.instantGame");
	/**
	 * 게임의 현재 상태
	 */
	private var state = GameState.STANDBY
		set(value) {
			if(field == value) return;
			field = value;
			if(value == GameState.PLAYING)
				Gdx.graphics.setForegroundFPS(Constants.FPS);
			else
				Gdx.graphics.setForegroundFPS(Constants.PASSIVE_FPS);  // 20fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
			invokeStateObservers();
		};
	/**
	 * 게임 상태 변경 감시자 목록
	 */
	private val stateObservers = GdxArray<Runnable>(false, 2, ArraySuppliers.runnable);
	/**
	 * 현재 라운드 (0이면 아직 게임이 시작되지 않은 것)
	 */
	var round = 0
		private set(value) {
			field = value;
			TitleBar.info = if(value > 0 && !hideRound) "Round $value" else null;
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
	 * 현재 게임이 일시 중지된 상태인지의 여부
	 */
	val isPaused: Boolean
		get() = (state == GameState.PAUSED);

	// setter에서만 한 번 쓰이므로 인라인
	private inline fun invokeStateObservers() {
		for(i in 0 until stateObservers.size)
			stateObservers.items[i].run();
	}

	/**
	 * 게임 상태가 바뀔 때 호출되는 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	fun attachStateObserver(handler: Runnable) {
		stateObservers.add(handler);
	}

	/**
	 * 게임 상태가 바뀔 때 호출되는 콜백 함수를 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	fun detachStateObserver(handler: Runnable) {
		stateObservers.removeValue(handler, true);
	}

	/**
	 * 준비 상태(타이틀 화면)로 전환한다.
	 */
	fun standBy() {
		// 월드를 메모리에서 내리기
		game.worldProjector.unloadWorld(true);

		// 통계, 시간 및 라운드 초기화
		resetAll();
		round = 0;
		TitleBar.stats = null;

		// 상태 전환
		state = GameState.STANDBY;

		// 타이틀 화면으로 전환
		game.setScreen(game.titleScreen);
	}

	/**
	 * 새 게임을 시작한다.
	 */
	fun newGame() {
		val player = game.player;

		// 통계 및 시간 초기화
		player.resetHealth();
		player.inventory.clear();
		resetAll();
		round++;

		// 상태 전환
		state = GameState.PLAYING;

		// 월드 생성 후 월드 표시기 화면으로 전환
		val world = ZombieWorld();
		player.world = world;
		player.teleportToCenter();
		world.entities.add(player);
		game.worldProjector.loadWorld(world, disposePrevious = true);
		if(game.getScreen() !== game.worldProjector)
			game.setScreen(game.worldProjector);
	}

	/**
	 * 게임을 종료 상태로 전환한다.
	 */
	fun setGameOver() {
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
	fun resume() {
		if(state != GameState.PAUSED)
			throw IllegalStateException("game is not paused");

		state = GameState.PLAYING;
	}

	/**
	 * 점수 및 통계 초기화
	 */
	fun resetAll() {
		scoreManager.resetScore();
		statistics.survivedDuration = 0;
		statistics.openedContainerCount = 0;
		statistics.killedZombieCount = 0;
		statistics.fireCount = 0;
		statistics.totalDamage = 0;
	}

	override fun update(delta: Float) {
		tickGlobalTimers(delta);

		if(state == GameState.PLAYING && !game.player.isAlive)
			setGameOver();
	}

	/**
	 * 전역 타이머 관리자를 갱신한다.
	 *
	 * update에서 한 번만 쓰이기 때문에 인라인이다.
	 * 
	 * @param delta 직전 프레임과의 간격(초)
	 */
	private inline fun tickGlobalTimers(delta: Float) {
		globalTimers.update(delta);
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
