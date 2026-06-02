package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Input;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.Window;
import io.potatogun.endlessdead.widget.Button;
import io.potatogun.endlessdead.world.ZombieWorld;

/**
 * 타이틀 화면
 */
class Title(override val game: EndlessDead) : Screen(game) {
	private val title = Textures.loadTexture("title/title.bmp");
	private val stillCut = Textures.loadTexture("title/still_cut.bmp");
	private var titleBlinkTimer = 0f;

	init {
		addWidget("play_button", Button({ Window.width * 0.5f - 130f }, { 120f }, 120f, caption = "Play", onClick = { startGame() }));
		addWidget("quit_button", Button({ Window.width * 0.5f + 10f }, { 120f }, 120f, caption = "Quit", onClick = { Gdx.app.exit() }));
	}

	private fun startGame() {
		GameManager.setPlaying();
		val worldViewer = game.getWorldViewer(ZombieWorldViewer::class);
		worldViewer.loadWorld(ZombieWorld(game, Constants.ZOMBIE_WORLD_WIDTH, Constants.ZOMBIE_WORLD_HEIGHT));
		game.setScreen(worldViewer);
		// Gdx.app.postRunnable { dispose() };
	}

	override fun update(delta: Float) {
		super.update(delta);

		titleBlinkTimer += delta;
		if(titleBlinkTimer >= 1f)
			titleBlinkTimer = 0f;
		if(Input.isAnyKeyJustPressed())
			startGame();
	}

	override fun drawBackground() {
		batch.draw(stillCut, 0f, 0f, Window.width, Window.height);
	}

	override fun drawElements() {
		val titleWidth = Window.width * 0.75f;
		val titleHeight = titleWidth / 6f;
		val titleX = (Window.width - titleWidth) * 0.5f;
		val titleY = Window.height * 0.5f + 80f;
		batch.draw(title, titleX, titleY, titleWidth, titleHeight);

		if(titleBlinkTimer % 1f < 0.5f)
			drawText(
				text = "Press any key to start",
				x = 0f,
				y = Window.height * 0.5f - 30f,
				color = Color.WHITE,
				scale = 1f,
				width = Window.width,
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
