package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Title;
import io.potatogun.endlessdead.screen.ZombieWorldProjector;
import io.potatogun.gdxhelper.Game;
import io.potatogun.gdxhelper.Window;

/**
 * 좀비 파밍 게임의 메인 본체
 */
class EndlessDead : Game() {
	// 미리 등록된 스크린. val은 lateinit이 불가하여 lazy 위임 사용.
	internal val titleScreen: Title by lazy { Title(this) };
	internal val worldViewer: ZombieWorldProjector by lazy { ZombieWorldProjector(this) };

	/**
	 * 게임 시작 시 한 번 호출되는 메쏘드
	 */
	override fun create() {
		setScreen(titleScreen);  // 부모 Game이 제공하는 메서드
		Window.setBaseTitle(Constants.GAME_TITLE);
	}

	// 게임 시간 갱신
	override fun render() {
		GameManager.tickGameTime(Gdx.graphics.getDeltaTime());
		super.render();
	}

	/**
	 * 자원을 정리한다.
	 */
	override fun dispose() {
		super.dispose();
		Textures.disposeShared();  // 공유 자원 정리
		Item.textures.disposeShared();
		titleScreen.dispose();  // 이게 로딩이 안 됐을 리가 없다.
		worldViewer.dispose();
	}
}
