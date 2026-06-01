package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Input;
import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.Timer;
import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.world.World;
import io.potatogun.endlessdead.world.ZombieWorld;
import io.potatogun.endlessdead.widget.Button;
import io.potatogun.endlessdead.widget.ProgressBar;
import io.potatogun.endlessdead.widget.Widget;
import io.potatogun.endlessdead.widget.style.ProgressBarStyle;

/**
 * 월드를 불러오고 월드를 화면에 프로젝션해주는 스크린이다.
 *
 * 객체 생성 이후 loadWorld(World)를 호출해야 한다. 
 */
class WorldViewer(game: EndlessDead) : Screen(game) {
	// 표시할 월드
	private var world: World? = null;
	private val frozenOverlay = Utils.rgb(0, 0, 0, 0.5f);
    private val solidColor: Texture;
	// 제목 표시줄에 표시할 정보의 인덱스
	private var currentTitleInfo = 0
		set(value) {
			if(value >= TitleInfoType.size) field = 0;
			else if(value < 0) field = TitleInfoType.size - 1;
			else field = value;
		};
	private val timers = mutableListOf<Timer>();
	// 자막 타이머 관련 필드들
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;
	// 재시작 단추.
	private val replayButton: Button;

	init {
		// 단색용 텍스처 생성
		Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
			setColor(Color.WHITE);
			fill();
			solidColor = Texture(this);
			dispose();
		};

		// 미터기 추가
		addWidget("hp_indicator", ProgressBar({ 80f }, { game.screenHeight - 24f }, 220f, color = Utils.rgb(234, 197, 21)));
		addWidget("gun_ammo_indicator", ProgressBar({ game.screenWidth - 145f }, { 10f }, 130f, color = Utils.rgb(15, 116, 240), style = ProgressBarStyle.CHUNKED).apply { hide() });
		addWidget("gun_cooldown_indicator", ProgressBar({ game.screenWidth - 215f }, { 10f }, 60f, value=0.42f, color = Color.SCARLET).apply { hide() });

		// 다시 시작 단추
		replayButton = Button({ game.screenWidth / 2 - 60f }, { 120f }, 120f, caption = "Replay", onClick = { restartGame() });

		// 제목 표시줄 정보 전환
		timers.add(Timer(3f, false) {
			if(!GameManager.isGameOver)
				currentTitleInfo++;
		}.register());
	}

	/**
	 * 지정한 월드를 불러와서 보여준다.
	 *
	 * @param world		불러올 월드
	 * @param dispose	기존 월드의 자원을 정리할지의 여부
	 */
	fun loadWorld(world: World, dispose: Boolean = false) {
		val previousWorld: World? = this.world;
		this.world = world;
		world.updateCamera();
		if(dispose) Gdx.app.postRunnable { previousWorld?.dispose() };
	}

	/**
	 * 월드를 언로딩한다.
	 *
	 * @param 	dispose	월드의 자원을 정리할지의 여부
	 * @return	성공 여부
	 */
	fun unloadWorld(dispose: Boolean = false): Boolean {
		val currentWorld: World? = world;
		if(currentWorld == null) return false;
		world = null;
		if(dispose) Gdx.app.postRunnable { currentWorld.dispose() };
		return true;
	}

	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		world?.onResize(width, height);
	}

	/**
	 * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경'은 이 안에서.
	 *
	 * 상태별로 해야 할 일이 완전히 다르므로 when 으로 분기한다.
	 * (입력 처리가 render() 가 아닌 update() 에 있는 이유:
	 *  '로직과 그리기의 분리' — render 는 매 프레임 그리는 일에만 집중하고,
	 *  상태 변화·입력은 update 가 책임진다.)
	 */
	override fun update(delta: Float) {
        when {
            GameManager.isPlaying	-> updateInPlay(delta);
            GameManager.isPaused	-> updatePaused();
            GameManager.isGameOver	-> updateGameOver();
        }
	}

    /**
	 * PLAYING 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크.
	 *
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun updateInPlay(delta: Float) {
		world?.update(delta);

		if(subtitlesTimer > 0f)
			subtitlesTimer -= delta;

		// 제목 표시줄에 통계 표시
		updateTitleBarInfo();

		// 미터기 정보 갱신
		updateProgressBars();

		// 일시 정지
		detectPauseKey();
    }

	/**
	 * 창 제목에 정보를 표시한다.
	 */
	private fun updateTitleBarInfo() {
		val world: World? = this.world;
		
		if(world == null) {
			game.setTitleBarStats(null);
			return;
		}
		
		game.setTitleBarStats(when(TitleInfoType.byIndex(currentTitleInfo)) {
			TitleInfoType.OPENED	-> "연 상자: ${world.player.openedContainerCount}개"
			TitleInfoType.KILLED	-> "잡은 좀비 수: ${world.player.killedZombieCount}"
			TitleInfoType.FIRED		-> "총 쏜 횟수: ${world.player.fireCount}"
			TitleInfoType.SURVIVED	-> "생존 시간: ${Utils.parseSeconds(world.player.survivedDuration, "분", "초")}"
			TitleInfoType.DAMAGE	-> "누적 피해량: ${world.player.totalDamage}"
			TitleInfoType.ZOMBIES	-> "현재 좀비 수: ${world.getEntities().filterIsInstance<Zombie>().size}"
		});
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
		
		val world: World? = this.world;
		if(world == null) {
			hpIndicator.hide();
			ammoIndicator.hide();
			cooldownIndicator.hide();
			return;
		}

		// HP 미터기 처리
		hpIndicator.apply {
			value = world.player.hp.toFloat() / world.player.maxHP;
			show();
		};

		// 총 관련 미터기 처리
		val holding: Item? = world.player.selectedItem;
		if(holding != null && holding is Gun) {
			// 총의 ammo를 미터기로 표시
			ammoIndicator.apply {
				value = holding.ammo.toFloat() / holding.maxAmmo;
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
	private inline fun updatePaused() {
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
		game.setTitleBarInfo("다시 시작하는 중...");
		// 불러오는 중이 막히지 않고 바로 뜨게 하기 위해 다음 프레임 때 로드
		Gdx.app.postRunnable {
			GameManager.setPlaying();  // 상태를 다시 플레이로 되돌리고
			game.currentRound++;
			loadWorld(ZombieWorld(game, this, Constants.ZOMBIE_WORLD_WIDTH.toFloat(), Constants.ZOMBIE_WORLD_HEIGHT.toFloat()), true);  // 월드를 아예 새로 파서 화면을 덮어씌움
			game.setTitleBarInfo(null);
		};
	}

	/**
	 * <Esc>나 P 글쇠가 눌렸을 때를 감지해서 일시 정지하거나 계속한다.
	 */
	private fun detectPauseKey() {
		// P키를 누르면 일시 정지 <-> 게임 진행 중 상태 토글!
		val resumeKeyPressed = Input.isKeyJustPressed(Input.SPACE) || Input.isButtonJustPressed(Input.LEFT_MOUSE);
        if(Input.isKeyJustPressed(Input.P) || Input.isKeyJustPressed(Input.ESCAPE) || resumeKeyPressed) {
            if(GameManager.isPlaying && !resumeKeyPressed)
				GameManager.pause();
			else if(GameManager.isPaused)
				GameManager.resume();
        }
	}

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI 를 얹는다.
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
		batch.draw(solidColor, 0f, 0f, game.screenWidth, game.screenHeight);
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
            y = game.screenHeight / 2f + 20f,
            color = Color.YELLOW,
            scale = 2.0f,
			width = game.screenWidth,
			align = Align.center,
			skipBatch = true
        );
        drawText(
            text = "Click anywhere or press <P> or <Esc> or <Space> to resume",
            x = 0f,
            y = game.screenHeight / 2f - 20f,
            color = Color.WHITE,
            scale = 1.0f,
			width = game.screenWidth,
			align = Align.center,
			skipBatch = true
        );
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
            y = game.screenHeight / 2f + 40f,
            color = Color.RED,
            scale = 2.0f,
			width = game.screenWidth,
			align = Align.center,
			skipBatch = true
        );
        drawText(
            text = "Press <Esc> to exit or press <R> or <Space> for a new game",
            x = 0f,
            y = game.screenHeight / 2f + 10f,
            color = Color.WHITE,
            scale = 1.0f,
			width = game.screenWidth,
			align = Align.center,
			skipBatch = true
        );

		// 통계
		val world: World? = this.world;
		if(world != null) {
			drawText(
				text = "Opened containers: ${world.player.openedContainerCount}",
				x = game.screenWidth / 2f - 70f,
				y = game.screenHeight / 2f - 20f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f,
				skipBatch = true
			);
			drawText(
				text = "Killed zombies: ${world.player.killedZombieCount}",
				x = game.screenWidth / 2f - 70f,
				y = game.screenHeight / 2f - 35f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f,
				skipBatch = true
			);
			drawText(
				text = "Fired: ${world.player.fireCount}",
				x = game.screenWidth / 2f - 70f,
				y = game.screenHeight / 2f - 50f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f,
				skipBatch = true
			);
			drawText(
				text = "Survived duration: ${Utils.parseSeconds(world.player.survivedDuration, "m", "s")}",
				x = game.screenWidth / 2f - 70f,
				y = game.screenHeight / 2f - 65f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f,
				skipBatch = true
			);
			drawText(
				text = "Total damage: ${world.player.totalDamage}",
				x = game.screenWidth / 2f - 70f,
				y = game.screenHeight / 2f - 80f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f,
				skipBatch = true
			);
			drawText(
				text = "Score: ${ScoreManager.score}",
				x = game.screenWidth / 2f - 70f,
				y = game.screenHeight / 2f - 95f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f,
				skipBatch = true
			);
		}

		// 다시 시작 단추 그리기(게임 오버 화면에서만 보임)
		drawWidget(replayButton);
    }

	/**
	 * 일반적으로 월드가 아닌 뷰어 자체의 배경은 없다(그려봤자 월드의 배경이 반투명하지 않는 이상 가려질 것이다).
	 */
	override fun drawBackground() {
		if(world != null) return;
		
		// 표시할 월드가 없을 때 보일 placeholder (일반적으로 볼 일은 없다.)
		drawText("No world loaded!", 0f, game.screenHeight / 2f, Color.RED, 1.0f, game.screenWidth, Align.center, true);
	}

	override fun drawElements() {
		// 월드 관련 처리...
		batch.end();  // 월드의 그리기 배치를 처리하기 전에 화면 자체의 배치를 잠시 중지.
		// 왜 World#render를 Screen#render가 아닌 drawElements에서 하냐고 묻는다면
		//   월드 뷰어 스크린 입장에서 월드는 이 스크린의 요소 중 하나일 뿐이기 때문이다.
		world?.render();
		batch.begin();  // 월드의 그리기가 끝나면 화면의 그리기 배치를 다시 시작

		// 자막이 있으면 표시
		if(subtitlesTimer > 0f) subtitlesMessage?.let {
			drawText(
				text = it,
				x = 0f,
				y = 20f,
				color = subtitlesColor,
				scale = 1.0f,
				width = game.screenWidth,
				align = Align.center,
				skipBatch = true
			);
		};

        // ── 항상 보이는 UI ──
        drawHud();
	}

    /**
	 * 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지.
	 * drawElements에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun drawHud() {
		val world: World? = this.world;
		if(world == null) return;

        // 1) UI 텍스트 (화면 고정) — 좌측 상단 HP 표시.
        //    카메라가 움직여도 항상 이 위치에 있다.
        drawText(
            text = "HP: ${world.player.hp}",
            x = 10f,
            y = game.screenHeight - 10f,   // 화면 y 축은 위로 증가 → 맨 위가 screenHeight
            color = Utils.rgb(255, 240, 128),
            scale = 1.2f,
			skipBatch = true
        );

		// 현재 플레이어가 들고 있는 아이템
		world.player.selectedItem?.let {
			drawText(
				text = "${it.name} [${world.player.selectedItemIndex!! + 1}/${world.player.inventoryItemCount}]",
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
            x = game.screenWidth - 130f,
            y = game.screenHeight - 10f,
            color = Utils.rgb(203, 241, 194),
            scale = 1.2f,
			width = 120f,
			align = Align.right,
			skipBatch = true
        );
    }

	/**
	 * 화면 하단에 자막을 표시한다.
	 *
	 * @param message	표시할 내용
	 * @param duration	표시 시간(초)
	 * @param color		글자 색
	 */
	fun drawSubtitles(message: String, duration: Int = 3, color: Color = Color.WHITE) {
		subtitlesTimer = duration.toFloat();
		subtitlesMessage = message;
		subtitlesColor = color;
	}
	
	override fun dispose() {
		super.dispose();
		solidColor.dispose();
		world?.dispose();
		for(timer in timers)
			timer.unregister();
		timers.clear();
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
