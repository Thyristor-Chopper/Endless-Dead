package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.Statistics;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.world.ZombieWorld;
import io.potatogun.gdxhelper.Input;
import io.potatogun.gdxhelper.Timer;
import io.potatogun.gdxhelper.TimerManager;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.screen.WorldViewer;
import io.potatogun.gdxhelper.widget.Button;
import io.potatogun.gdxhelper.widget.ProgressBar;
import io.potatogun.gdxhelper.widget.Widget;
import io.potatogun.gdxhelper.world.World;

/**
 * WorldViewer 자체는 정말 월드 자체만을 보여주는 기본적인 뷰어이다.
 *
 * 이를 확장하여 HUD나 다채로운 화면을 구현해보자.
 *
 * 이쪽에는 우리 게임의 목적에 맞게 HP 미터기, 일시 정지 처리, 게임 오버 화면 등이 모두 구현되어 있다.
 */
class ZombieWorldViewer(private val game: EndlessDead) : WorldViewer(), SubtitlesDrawable {
	private val noWorldOverlay = Utils.rgb(255, 255, 255, 0.5f);
	private val frozenOverlay = Utils.rgb(0, 0, 0, 0.5f);
    private val solidColor: Texture;
	// 제목 표시줄에 표시할 정보의 인덱스
	private var currentTitleInfo = 0
		set(value) {
			if(value >= TitleInfoType.size) field = 0;
			else if(value < 0) field = TitleInfoType.size - 1;
			else field = value;
		};
	// 일시 중지 및 게임 오버 화면의 단추.
	private val resumeButton: Button;
	private val replayButton: Button;
	private val titleButton: Button;
	private val quitButton: Button;
	// 로드된 월드가 없을 때 보일 placeholder 배경
	private val lazyStillCut = lazy { Utils.loadTexture("title/still_cut.bmp") };
	// 타이머
	private val timerManager = TimerManager();
	// 자막 타이머 관련 필드들. 우리가 만든 Timer 객체와 달리 일정 시간 간격으로 '계속' 실행하는 그런 게 아니기 때문에 따로 관리.
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;

	init {
		// 단색용 텍스처 생성
		Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
			setColor(Color.WHITE);
			fill();
			solidColor = Texture(this);
			dispose();
		};

		// 미터기 추가
		addWidget("hp_indicator", ProgressBar({ 80f }, { Window.height - 24f }, 220f, color = Utils.rgb(234, 197, 21)));
		addWidget("gun_ammo_indicator", ProgressBar({ Window.width - 145f }, { 10f }, 130f, color = Utils.rgb(15, 116, 240), style = ProgressBar.Style.CHUNKED).apply { hide() });
		addWidget("gun_cooldown_indicator", ProgressBar({ Window.width - 215f }, { 10f }, 60f, value=0.42f, color = Color.SCARLET).apply { hide() });

		// 일시 중지 및 게임 오버 단추
		resumeButton = Button({ Window.width * 0.5f - 195f }, { 120f }, 120f, caption = "Resume") {
			GameManager.resume();
		};
		replayButton = Button({ Window.width * 0.5f - 195f }, { 120f }, 120f, caption = "Continue") {
			restartGame();
		};
		titleButton = Button({ Window.width * 0.5f - 60f }, { 120f }, 120f, caption = "Back to title") {
			unloadWorld(dispose = true);
			GameManager.resetAll();
			GameManager.standBy();
			game.setScreen(game.titleScreen);
		};
		quitButton = Button({ Window.width * 0.5f + 75f }, { 120f }, 120f, caption = "Quit") {
			Gdx.app.exit();
		};

		// 제목 표시줄 정보 전환
		timerManager.registerTimer(Timer(3f) {
			currentTitleInfo++;
		});
	}

	// 창 최소화(아이콘 표시) 시 자동 일시 중지
	override fun pause() {
		if(GameManager.isPlaying)
			GameManager.pause();
	}

	/**
	 * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경'은 이 안에서.
	 *
	 * 상태별로 해야 할 일이 완전히 다르므로 when으로 분기한다.
	 * (입력 처리가 render()가 아닌 update() 에 있는 이유:
	 *  '로직과 그리기의 분리' — render는 매 프레임 그리는 일에만 집중하고,
	 *  상태 변화·입력은 update가 책임진다.)
	 */
	override fun update(delta: Float) {
		when {
            GameManager.isPlaying	-> updatePlaying(delta);
            GameManager.isPaused	-> updatePaused(delta);
            GameManager.isGameOver	-> updateGameOver();
        }
	}

    /**
	 * PLAYING 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크.
	 *
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun updatePlaying(delta: Float) {
		timerManager.tick(delta);

		// 제목 표시줄에 통계 표시
		updateTitleBarInfo();

		super.update(delta);

		// 자막 만료 타이머 갱신
		if(subtitlesTimer > 0f)
			subtitlesTimer -= delta;

		// 미터기 정보 갱신
		updateProgressBars();

		// 일시 정지
		detectPauseKey();
    }

	/**
	 * 창 제목에 정보를 표시한다.
	 */
	private fun updateTitleBarInfo() {
		val world: World? = projectingWorld;
		val player: Player? = world?.get<Player>();
		if(world == null || player == null) {
			Window.titleBarStats = null;
			return;
		}
		
		Window.titleBarStats = when(TitleInfoType.byIndex(currentTitleInfo)) {
			TitleInfoType.OPENED	-> "Opened chests: ${Statistics.openedContainerCount}"
			TitleInfoType.KILLED	-> "Killed zombies: ${Statistics.killedZombieCount}"
			TitleInfoType.FIRED		-> "Fired: ${Statistics.fireCount}"
			TitleInfoType.SURVIVED	-> "Survived duration: ${Utils.parseSeconds(Statistics.survivedDuration, "m", "s")}"
			TitleInfoType.DAMAGE	-> "Total damage: ${Statistics.totalDamage}"
			TitleInfoType.ZOMBIES	-> "Current zombies: ${world.getEntities().filterIsInstance<Zombie>().size}"
		};
	}

	/**
	 * 미터기 정보를 갱신한다.
	 *
	 * updateInPlay에서만 한 번 쓰이기 때문에 inline이다.
	 */
	private inline fun updateProgressBars() {
		val hpIndicator = getWidget("hp_indicator") as ProgressBar;
		val ammoIndicator = getWidget("gun_ammo_indicator") as ProgressBar;
		val cooldownIndicator = getWidget("gun_cooldown_indicator") as ProgressBar;

		val world: World? = projectingWorld;
		val player: Player? = world?.get<Player>();
		if(world == null || player == null) {
			hpIndicator.hide();
			ammoIndicator.hide();
			cooldownIndicator.hide();
			return;
		}

		// HP 미터기 처리
		hpIndicator.apply {
			value = player.hp.toFloat() / player.maxHP;
			show();
		};

		// 총 관련 미터기 처리
		val holding: Item? = player.selectedItem;
		if(holding != null && holding is Gun) {
			// 총의 ammo를 미터기로 표시
			ammoIndicator.apply {
				value = holding.remainingBullets.toFloat() / holding.maxBullets;
				show();
			};

			// 총의 공격 쿨타임 표시
			if(holding.fireInterval > 0.2f) {
				val cooldown = holding.getRemainingCooldownPercentage();
				if(cooldown > 0f)
					cooldownIndicator.apply {
						value = cooldown;
						show();
					};
				else
					cooldownIndicator.hide();
			}
		} else {
			ammoIndicator.hide();
			cooldownIndicator.hide();
		}
	}

	/**
	 * 일시 정지 상태에서 매 프레임 로직
	 *
	 * 객체 업데이트(super.update)나 타이머(spawner.tick)를 호출하지 않음.
	 *   세상이 그대로 멈춰있는 상태가 됨
	 *
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
	private inline fun updatePaused(delta: Float) {
		timerManager.tick(delta);

		// 제목 표시줄에 통계 표시
		updateTitleBarInfo();
		
		// 일시 정지 키 누름 감지
		detectPauseKey();
    }

    /**
	 * 게임 오버 상태에서 매 프레임 처리
	 *
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun updateGameOver() {
        // ESC 키가 '막 눌린 순간' 앱 종료.
        //   isKeyJustPressed로 한 이유: 누르고 있는 동안 매 프레임 exit이 호출되지 않게.
        if(Input.isKeyJustPressed(Input.ESCAPE))
            Gdx.app.exit();

        // R 키나 사이띄개를 누르면 다시 시작
        if(Input.isKeyJustPressed(Input.R) || Input.isKeyJustPressed(Input.SPACE))
			restartGame();
    }

	private fun restartGame() {
		GameManager.resetAll();
		GameManager.setPlaying();  // 상태를 다시 플레이로 되돌리고
		loadWorld(ZombieWorld(), disposePreviousWorld = true);  // 월드를 아예 새로 파서 화면을 덮어씌움
	}

	/**
	 * <Esc>나 P 글쇠가 눌렸을 때를 감지해서 일시 정지하거나 계속한다.
	 */
	private fun detectPauseKey() {
		// P키를 누르면 일시 정지 <-> 게임 진행 중 상태 토글!
		val resumeKeyPressed = Input.isKeyJustPressed(Input.SPACE);
        if(Input.isKeyJustPressed(Input.P) || Input.isKeyJustPressed(Input.ESCAPE) || resumeKeyPressed) {
            if(GameManager.isPlaying && !resumeKeyPressed)
				GameManager.pause();
			else if(GameManager.isPaused)
				GameManager.resume();
        }
	}

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI를 얹는다.
     *
     * 이 함수에서는 '그리기'만 한다. 입력 처리·상태 변경은 update()의 책임.
     *
     * 주의: super.render(delta) 가 화면 clear + 배경 + 객체까지 그리므로,
     *       텍스트는 반드시 super 호출 '이후' 그려야 가려지지 않는다.
     */
	override fun render(delta: Float) {
		super.render(delta);

		batch.begin();

		// 일시 정지 시 어둡게 변경
		if(!GameManager.isPlaying)
			drawFrozenOverlay();

        // ── 상태별로 그리는 것이 다름 ──
        when {
            // 플레이 중에는 추가로 그릴 것 없음
            GameManager.isPaused	-> drawPausedMessage();  // 일시정지 화면 그리기
            GameManager.isGameOver	-> drawGameOverMessage();
        }

		batch.end();
	}

	/**
	 * 화면에 어두운 오버레이(주로 모달용)를 만든다.
	 *
	 * render에서만 한 번 쓰이기 때문에 inline이다.
	 */
	private inline fun drawFrozenOverlay() {
		batch.color = frozenOverlay;
		batch.draw(solidColor, 0f, 0f, Window.width, Window.height);
		batch.color = Color.WHITE;
	}

	/**
	 * 일시 정지 시 띄우는 메시지
	 *
	 * render에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun drawPausedMessage() {
        drawText(
            text = "PAUSED",
            x = 0f,
            y = Window.height * 0.5f + 20f,
            color = Color.YELLOW,
            scale = 2.0f,
			width = Window.width,
			align = Align.center,
			skipBatch = true
        );
        drawText(
            text = "Press <P> or <Esc> or <Space> to resume",
            x = 0f,
            y = Window.height * 0.5f - 20f,
            color = Color.WHITE,
            scale = 1.0f,
			width = Window.width,
			align = Align.center,
			skipBatch = true
        );

		drawWidget(resumeButton);
		drawWidget(titleButton);
		drawWidget(quitButton);
    }

    /**
	 * 게임 오버 시 화면 중앙에 띄우는 안내 메시지
	 *
	 * render에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun drawGameOverMessage() {
        drawText(
            text = "YOU DIED!",
            x = 0f,
            y = Window.height * 0.5f + 40f,
            color = Color.RED,
            scale = 2.0f,
			width = Window.width,
			align = Align.center,
			skipBatch = true
        );
        drawText(
            text = "Press <Esc> to exit or press <R> or <Space> to continue",
            x = 0f,
            y = Window.height * 0.5f + 10f,
            color = Color.WHITE,
            scale = 1.0f,
			width = Window.width,
			align = Align.center,
			skipBatch = true
        );

		// 통계
		drawText(
			text = "Opened containers: ${Statistics.openedContainerCount}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 20f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f,
			skipBatch = true
		);
		drawText(
			text = "Killed zombies: ${Statistics.killedZombieCount}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 35f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f,
			skipBatch = true
		);
		drawText(
			text = "Fired: ${Statistics.fireCount}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 50f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f,
			skipBatch = true
		);
		drawText(
			text = "Survived duration: ${Utils.parseSeconds(Statistics.survivedDuration, "m", "s")}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 65f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f,
			skipBatch = true
		);
		drawText(
			text = "Total damage: ${Statistics.totalDamage}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 80f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f,
			skipBatch = true
		);
		drawText(
			text = "Final score: ${ScoreManager.score}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 95f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f,
			skipBatch = true
		);

		// 게임 오버 관련 단추 그리기(게임 오버 화면에서만 보임)
		drawWidget(replayButton);
		drawWidget(titleButton);
		drawWidget(quitButton);
    }

	/**
	 * 로딩된 월드가 없을 때 placeholder 배경
	 */
	override fun drawBackground() {
		if(projectingWorld != null) return;

		// 표시할 월드가 없을 때 보일 placeholder (일반적으로 볼 일은 없다.)
		batch.color = noWorldOverlay;
		batch.draw(lazyStillCut.value, 0f, 0f, Window.width, Window.height);
		batch.color = Color.WHITE;
		drawText("No world loaded!", 0f, Window.height * 0.5f, Color.SCARLET, 2.0f, Window.width, Align.center, true);
	}

	override fun drawElements() {
		// 월드 및 자막 그리기
		super.drawElements();

		// 자막이 있으면 표시
		if(subtitlesTimer > 0f) subtitlesMessage?.let {
			drawText(
				text = it,
				x = 0f,
				y = 20f,
				color = subtitlesColor,
				scale = 1.0f,
				width = Window.width,
				align = Align.center,
				skipBatch = true
			);
		};

        // ── 항상 보이는 UI ──
        drawHud();
	}

	override fun drawSubtitles(message: String, duration: Int, color: Color) {
		subtitlesTimer = duration.toFloat();
		subtitlesMessage = message;
		subtitlesColor = color;
	}

    /**
	 * 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지.
	 * drawElements에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun drawHud() {
		val world: World? = projectingWorld;
		val player: Player? = world?.get<Player>();
		if(world == null || player == null) return;

        // 1) UI 텍스트 (화면 고정) — 좌측 상단 HP 표시.
        //    카메라가 움직여도 항상 이 위치에 있다.
        drawText(
            text = "HP: ${player.hp}",
            x = 10f,
            y = Window.height - 10f,   // 화면 y 축은 위로 증가 → 맨 위가 screenHeight
            color = Utils.rgb(255, 240, 128),
            scale = 1.2f,
			skipBatch = true
        );

		// 현재 플레이어가 들고 있는 아이템
		player.selectedItem?.let {
			drawText(
				text = "${it.name} [${player.selectedItemIndex!! + 1}/${player.inventoryItemCount}]",
				x = 10f,
				y = 20f,
				color = Utils.rgb(255, 255, 192),
				scale = 1.0f,
				skipBatch = true
			);
		};

		// 점수
		drawText(
            text = "Score: ${ScoreManager.score}",
            x = Window.width - 130f,
            y = Window.height - 10f,
            color = Utils.rgb(203, 241, 194),
            scale = 1.2f,
			width = 120f,
			align = Align.right,
			skipBatch = true
        );
    }
	
	override fun dispose() {
		super.dispose();
		solidColor.dispose();
		if(lazyStillCut.isInitialized())
			lazyStillCut.value.dispose();
		timerManager.clearTimers();
	}

	/**
	 * 제목 표시줄에 표시할 정보 종류를 담는 열거형
	 */
	private enum class TitleInfoType {
		OPENED,
		KILLED,
		FIRED,
		SURVIVED,
		DAMAGE,
		ZOMBIES;

		companion object {
			private val enumEntries = TitleInfoType.entries;
			val size = enumEntries.size;

			fun byIndex(index: Int) = enumEntries[index];
		}
	}
}
