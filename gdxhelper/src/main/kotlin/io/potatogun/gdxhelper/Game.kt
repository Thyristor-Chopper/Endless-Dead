package io.potatogun.gdxhelper;

import com.badlogic.gdx.Game as GdxGame;

import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.WorldViewer;

abstract class Game : GdxGame() {
	/**
	 * Gdx.graphics.width를 매번 실수형으로 변환하는 오버헤드를 없애기 위해 창 크기를 캐시하고 크기가 바뀔 때만 업데이트한다.
	 */
	override fun resize(width: Int, height: Int) {
		Window.updateWindowDimensions();
		super.resize(width, height);
	}

	/**
	 * 자원을 정리한다.
	 */
	override fun dispose() {
		super.dispose();
		HelperTextures.disposeShared();
	}
}
