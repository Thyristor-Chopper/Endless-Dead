package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.Screen;
import io.potatogun.gdxhelper.util.isMobile as isMobilePlatform;
import io.potatogun.gdxhelper.util.loadTexture;

/**
 * 타이틀 화면
 */
class Title : Screen(font = EndlessDead.instance.resources.font) {
	private val game = EndlessDead.instance;
	private val title = loadTexture("title/title.bmp");
	private val stillCut = loadTexture("title/still_cut.bmp");
	private val stillCutTint = Color.GRAY;  // 원래보다 반 정도 어둡게
	private var titleBlinkTimer = 0f;
	private val isMobile = isMobilePlatform;

	init {
		val resources = game.resources;

		val playButton = TextButton("Play", resources.primaryButton).apply {
			addListener(object : ClickListener() {
				override fun clicked(event: InputEvent, x: Float, y: Float) {
					game.gameManager.newGame();
				}
			});
		};
		val quitButton = TextButton("Quit", resources.button).apply {
			addListener(object : ClickListener() {
				override fun clicked(event: InputEvent, x: Float, y: Float) {
					Gdx.app.exit();
				}
			});
		};
		val table = Table().apply {
			setFillParent(true);
			defaults().pad(10f).width(120f).height(25f);
			add(playButton);
			add(quitButton);
			bottom();
			y = 120f;
		}.also { stage.addActor(it) };
	}

	override fun update(delta: Float) {
		super.update(delta);

		if(!isMobile) {
			titleBlinkTimer += delta;
			if(titleBlinkTimer >= 1f)
				titleBlinkTimer = 0f;
		}
	}

	override fun onKeyDown(code: Int): Boolean {
		super.onKeyDown(code);
		game.gameManager.newGame();
		return true;
	}

	override fun drawBackground() {
		batch.color = stillCutTint;
		batch.draw(stillCut, 0f, 0f, Window.width, Window.height);
		batch.color = Color.WHITE;
	}

	override fun drawContents() {
		// 아무 키나 누르시오. 구현,
		//   titleBlinkTimer로 깜박이는 간격 구현
		val titleWidth = Window.width * 0.75f;
		val titleHeight = titleWidth / 6f;
		val titleX = (Window.width - titleWidth) * 0.5f;
		val titleY = Window.height * 0.5f + 80f;
		batch.draw(title, titleX, titleY, titleWidth, titleHeight);
		if(!isMobile && titleBlinkTimer % 1f < 0.5f)
			drawText(
				text = "Press any key to start",
				x = 0f,
				y = Window.height * 0.5f - 30f,
				color = Color.WHITE,
				scale = 1f,
				width = Window.width,
				align = Align.center
			);
	}

	override fun dispose() {
		super.dispose();
		title.dispose();
		stillCut.dispose();
	}
}
