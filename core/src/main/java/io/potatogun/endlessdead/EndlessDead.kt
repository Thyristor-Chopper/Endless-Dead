package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Title;
import io.potatogun.endlessdead.screen.ZombieWorldProjector;
import io.potatogun.gdxhelper.Game;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.WorldProjector;
import io.potatogun.gdxhelper.util.getGameInstance;
import io.potatogun.gdxhelper.util.isMobile;
import io.potatogun.gdxhelper.world.World;

/**
 * 좀비 파밍 게임의 메인 본체
 */
class EndlessDead : Game() {
	/**
	 * 이 게임 인스턴스의 리소스
	 */
	@get:JvmName("getResourceInstance") lateinit var resources: Resources  // create에서 초기화 후 바뀌지 않음
		private set;
	/**
	 * 현재 메인 게임 플레이어
	 *
	 * 자바에서는 getPlayer()로 가져온다.
	 */
	@set:JvmSynthetic var player: Player? = null
		private set;
	/**
	 * 게임 관리자
	 */
	@JvmField val gameManager = GameManager(this);
	// 화면들
	@get:JvmSynthetic internal lateinit var titleScreen: Title  // create에서 초기화 후 바뀌지 않음
		private set;
	@get:JvmSynthetic internal lateinit var worldProjector: WorldProjector  // create에서 초기화 후 바뀌지 않음
		private set;

	// 게임 시작 시 한 번 호출되는 메쏘드
	override fun create() {
		super.create();

		resources = Resources();
		titleScreen = Title();
		worldProjector = ZombieWorldProjector();

		if(isMobile) {
			val width = Window.realWidth.toFloat();
			val height = Window.realHeight.toFloat();
			val dp = width / Gdx.graphics.density;
			val fixedWidth = (dp * 1f).toInt();
			val fixedHeight = (dp * (height / width)).toInt();
			Window.setFixedSize(fixedWidth, fixedHeight);
		}

		if(java.lang.Boolean.getBoolean("zombie.instantGame")) {
			gameManager.newGame();
		} else {
			setScreen(titleScreen);
		}

		TitleBar.base = Constants.GAME_TITLE;
	}

	// 게임 갱신
	override fun update(delta: Float) {
		super.update(delta);

		gameManager.update(delta);
	}

	@SinceKotlin("9999.9") fun createPlayer(): Player = createPlayer(null, 0f, 0f);

	@SinceKotlin("9999.9") fun createPlayer(world: World?): Player = createPlayer(world, 0f, 0f);

	fun createPlayer(world: World? = null, x: Float = 0f, y: Float = 0f): Player {
		if(player != null)
			throw IllegalStateException("player already exists");
		return Player(world, x, y).also { player = it };
	}

	@SinceKotlin("9999.9") fun recreatePlayer(): Player = recreatePlayer(null, 0f, 0f);

	@SinceKotlin("9999.9") fun recreatePlayer(world: World?): Player = recreatePlayer(world, 0f, 0f);

	fun recreatePlayer(world: World? = null, x: Float = 0f, y: Float = 0f): Player {
		player?.remove();
		return Player(world, x, y).also { player = it };
	}

	fun deletePlayer() {
		player?.remove();
		player = null;
	}

	// 자원 정리
	override fun dispose() {
		super.dispose();
		if(Item.Textures.isInstanceCreated(this))
			Item.Textures.getInstance(this).dispose();
		titleScreen.dispose();
		worldProjector.dispose();
		player?.dispose();
		resources.dispose();
	}

	companion object {
		/**
		 * 게임의 현재 인스턴스
		 *
		 * 자바에서는 getInstance()로 호출한다.
		 *
		 * @throws NullPointerException 프로그램 자체가 초기화되기 전에 접근할 경우.
		 */
		val instance: EndlessDead
			inline get() = getGameInstance<EndlessDead>();
	}
}
