package com.oop.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;

import com.oop.game.GameManager;
import com.oop.game.GameState;
import com.oop.game.Input;
import com.oop.game.ZombieGame;
import com.oop.game.world.ZombieWorld;

/**
 * 타이틀 화면이다.
 */
class Title(game: ZombieGame) : Screen(game) {
	private val title = Texture(Gdx.files.internal("title.bmp"));
	private val stillCut = Texture(Gdx.files.internal("still_cut.bmp"));
	private var titleBlinkTimer = 0f;
	
	override fun update(delta: Float) {
		titleBlinkTimer += delta;
		if(Input.isAnyKeyJustPressed() || Input.isButtonJustPressed(Input.LEFT_MOUSE)) {
			game.setTitleBarInfo("불러오는 중...");
			GameManager.setPlaying()  // 타이틀 화면 설정
			// 불러오는 중이 막히지 않고 바로 뜨게 하기 위해 다음 프레임 때 로드
			Gdx.app.postRunnable {
				game.currentRound = 1;
				game.setScreen(ZombieWorld(game));
				game.setTitleBarInfo(null);
				Gdx.app.postRunnable { dispose() };
			};
		}
	}
	
	override fun drawBackground() {
		batch.draw(stillCut, 0f, 0f, game.screenWidth.toFloat(), game.screenHeight.toFloat());
	}
	
	override fun drawElements(delta: Float) {
		drawTitle();
	}
	
	private inline fun drawTitle() {
		val titleWidth = game.screenWidth * 0.75f;
		val titleHeight = titleWidth / 6f;
		val titleX = (game.screenWidth - titleWidth) / 2f;
		val titleY = game.screenHeight / 2f + 80f;

		batch.draw(title, titleX, titleY, titleWidth, titleHeight);
		if(titleBlinkTimer % 1f < 0.5f)
			drawText(
				text = "Press any key to start",
				x = 0f,
				y = game.screenHeight / 2f - 30f,
				color = Color.WHITE,
				scale = 1f,
				width = game.screenWidth.toFloat(),
				align = Align.center,
				skipBatch = true
			);
		if(titleBlinkTimer >= 1f)
			titleBlinkTimer = 0f;
	}
	
	override fun dispose() {
		super.dispose();
		title.dispose();
		stillCut.dispose();
	}
}
