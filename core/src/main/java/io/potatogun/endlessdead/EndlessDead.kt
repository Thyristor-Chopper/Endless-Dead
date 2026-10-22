package io.potatogun.endlessdead;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Title;
import io.potatogun.endlessdead.screen.ZombieWorldProjector;
import io.potatogun.gdxhelper.Game;
import io.potatogun.gdxhelper.screen.WorldProjector;

/**
 * 좀비 파밍 게임의 메인 본체
 */
class EndlessDead : Game() {
	// lazy 정의
	private val lzPlayer = lazy { Player() };
	private val lzTitleScreen = lazy { Title(this) };
	private val lzWorldProjector = lazy { ZombieWorldProjector(this) };
	/**
	 * 게임 플레이어
	 *
	 * 자바에서는 getPlayer()를 쓴다.
	 */
	val player: Player by lzPlayer;
	/**
	 * 게임 관리자
	 */
	@JvmField val gameManager = GameManager(this);
	// 미리 등록된 스크린. val은 lateinit이 불가하여 lazy 위임 사용.
	@get:JvmSynthetic internal val titleScreen: Title by lzTitleScreen;
	@get:JvmSynthetic internal val worldProjector: WorldProjector by lzWorldProjector;

	// 게임 시작 시 한 번 호출되는 메쏘드
	override fun create() {
		instance = this;

		if(java.lang.Boolean.getBoolean("zombie.instantGame")) {
			gameManager.newGame();
		} else {
			setScreen(titleScreen);
		}

		TitleBar.base = Constants.GAME_TITLE;
	}

	// 게임 시간 갱신
	override fun update(delta: Float) {
		super.update(delta);

		gameManager.update(delta);
	}

	// 자원을 정리한다.
	override fun dispose() {
		super.dispose();
		Resources.dispose();  // 공유 자원 정리
		Item.textures.dispose();
		if(lzTitleScreen.isInitialized())
			titleScreen.dispose();
		if(lzWorldProjector.isInitialized())
			worldProjector.dispose();
		if(lzPlayer.isInitialized())
			player.finalDispose();
	}

	companion object {
		/**
		 * 게임의 인스턴스
		 *
		 * libGDX는 Game()의 인스턴스가 두 개 이상일 이유가 없고 창을 두 개 이상 띄우지 못하므로 큰 위험성은 없다.
		 */
		@get:JvmStatic lateinit var instance: EndlessDead  // 바뀌지 않는 필드임.
			private set;
	}
}
