package io.potatogun.gdxhelper;

import com.badlogic.gdx.Game as GdxGame;
import com.badlogic.gdx.Gdx;

import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.Screen;
import io.potatogun.gdxhelper.world.World;

/**
 * 이 프레임워크에 맞게 일부 처리가 추가된 Game 추상 클래스
 */
abstract class Game : GdxGame() {
	// Gdx.graphics.width를 매번 실수형으로 변환하는 오버헤드를 없애기 위해 창 크기를 캐시하고 크기가 바뀔 때만 업데이트한다.
	override fun resize(width: Int, height: Int) {
		Window.updateWindowDimensions();
		val screen = getScreen();
		if(screen !is Screen) {
			super.resize(width, height);
			return;
		}
		screen.updateProjectionMatrix();
		screen.resize(width, height);
	}

	override fun render() {
		val screen = getScreen();
		if(screen !is Screen) {
			super.render();
			return;
		}
		val delta = Gdx.graphics.getDeltaTime();
		screen.update(delta);
		screen.render();
	}

	// 자원 정리
	override fun dispose() {
		super.dispose();
		HelperTextures.disposeShared();
		World.disposeUndispoed();
	}
}
