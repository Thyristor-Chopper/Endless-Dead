package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.ArraySuppliers;
import io.potatogun.gdxhelper.util.Updatable;
import io.potatogun.gdxhelper.util.beep3;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.teleportToCenter;
import io.potatogun.endlessdead.world.ZombieWorld;

/**
 * 게임 상태 관리자
 *
 * @property game 게임 인스턴스
 */
class GameManager(private val game: EndlessDead) : Updatable {
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
		game.deletePlayer();
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
		// 통계 및 시간 초기화
		game.deletePlayer();
		round++;

		// 월드 및 플레이어 생성
		val world = ZombieWorld();
		val player = game.createPlayer(world).apply { teleportToCenter() };
		world.entities.add(player);

		// 상태 전환
		state = GameState.PLAYING;

		// 월드 생성 후 월드 표시기 화면으로 전환
		if(game.getScreen() !== game.worldProjector)
			game.setScreen(game.worldProjector);
		game.worldProjector.loadWorld(world, disposePrevious = true);
	}

	/**
	 * 게임을 종료 상태로 전환한다.
	 */
	fun setGameOver() {
		state = GameState.GAME_OVER;
		game.deletePlayer();
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

	override fun update(delta: Float) {
		val player = game.player;
		if(state == GameState.PLAYING && player != null && !player.isAlive) {
			beep3();
			setGameOver();
		}
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
