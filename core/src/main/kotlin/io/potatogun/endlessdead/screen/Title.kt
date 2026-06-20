package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.world.ZombieWorld;
import io.potatogun.gdxhelper.Input;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.Screen;
import io.potatogun.gdxhelper.widget.Button;

/**
 * 타이틀 화면
 *
 * @property game 게임 인스턴스
 */
class Title(private val game: EndlessDead) : Screen() {
	private val title = Utils.loadTexture("title/title.bmp");
	private val stillCut = Utils.loadTexture("title/still_cut.bmp");
	private var titleBlinkTimer = 0f;

	init {
		addWidget("play_button", Button({ Window.width * 0.5f - 130f }, { 120f }, 120f, caption = "Play", skin = Textures.greenButton) { startGame() });
		addWidget("quit_button", Button({ Window.width * 0.5f + 10f }, { 120f }, 120f, caption = "Quit", color = Utils.rgb(225, 247, 231)) { Gdx.app.exit() });
	}

	private fun startGame() {
		GameManager.resetAll();
		GameManager.setPlaying();
		val worldViewer = game.worldViewer.apply { loadWorld(ZombieWorld()) };
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

	// 아무 키나 누르시오. 구현,
	//   titleBlinkTimer로 깜박이는 간격 구현
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
