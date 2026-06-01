package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Input;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.widget.Button;
import io.potatogun.endlessdead.world.ZombieWorld;

/**
 * 타이틀 화면
 */
class Title(game: EndlessDead) : Screen(game) {
	private val title = Textures.loadTexture("title.bmp");
	private val stillCut = Textures.loadTexture("still_cut.bmp");
	private var titleBlinkTimer = 0f;

	init {
		addWidget("play_button", Button({ game.screenWidth / 2 - 125f }, { 120f }, 120f, caption = "Play", onClick = { startGame() }));
		addWidget("quit_button", Button({ game.screenWidth / 2 + 5f }, { 120f }, 120f, caption = "Quit", onClick = { Gdx.app.exit() }));
	}

	private fun startGame() {
		GameManager.setPlaying();
		game.currentRound = 1;
		game.setScreen(WorldViewer(game).apply { loadWorld(ZombieWorld(game, this, Constants.ZOMBIE_WORLD_WIDTH.toFloat(), Constants.ZOMBIE_WORLD_HEIGHT.toFloat())) });
		game.setTitleBarInfo(null);
		Gdx.app.postRunnable { dispose() };
	}

	override fun update(delta: Float) {
		titleBlinkTimer += delta;
		if(titleBlinkTimer >= 1f)
			titleBlinkTimer = 0f;
		if(Input.isAnyKeyJustPressed())
			startGame();
	}

	override fun drawBackground() {
		batch.draw(stillCut, 0f, 0f, game.screenWidth, game.screenHeight);
	}

	override fun drawElements() {
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
				width = game.screenWidth,
				align = Align.center,
				skipBatch = true
			);
	}

	override fun dispose() {
		super.dispose();
		title.dispose();
		stillCut.dispose();
	}
}
