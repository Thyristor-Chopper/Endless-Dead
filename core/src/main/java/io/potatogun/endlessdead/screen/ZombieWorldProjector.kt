package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.Statistics;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.entity.isSameTeamWith;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.endlessdead.world.ZombieWorld;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.entity.manager.countOf;
import io.potatogun.gdxhelper.entity.manager.getDistanceSorted;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.screen.WorldProjector;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.Input;
import io.potatogun.gdxhelper.util.TextureUtils;
import io.potatogun.gdxhelper.util.Utils;
import io.potatogun.gdxhelper.widget.Button;
import io.potatogun.gdxhelper.widget.ProgressBar;
import io.potatogun.gdxhelper.world.World;

/**
 * 게임 목적에 맞게 HUD와 체력 정보, 총 정보 등을 표시해주는 월드 뷰어.
 *   이쪽에는 우리 게임의 목적에 맞게 HP 미터기, 일시 정지 처리, 게임 오버 화면 등이 모두 구현되어 있다.
 *
 * @property game 게임 인스턴스
 */
class ZombieWorldProjector(private val game: EndlessDead) : WorldProjector(), SubtitlesDrawable {
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
	private val lazyStillCut = lazy { TextureUtils.loadTexture("title/still_cut.bmp") };
	// 타이머
	private val timerManager = TimerManager();
	// 자막 관련 필드들.
	private var subtitlesTimer: Timer? = null;
	private var subtitlesMessage: String = "";
	private var subtitlesColor = Color.WHITE;
	private val subtitlesVisible: Boolean
		inline get() = (subtitlesTimer != null);
	private var attackTarget: LivingEntity? = null;  // 매 업데이트 시 개체가 죽으면 초기화되므로 굳이 WeakReference 쓸 필요 없음
	private val enemyBarColor = Utils.rgb(205, 46, 46);
	private val friendBarColor = Utils.rgb(132, 208, 132);
	private val playerNameColor = Utils.rgb(156, 213, 155);
	private val enemyNameColor = Utils.rgb(247, 215, 215);
	private val friendNameColor = Utils.rgb(215, 247, 215);
	private val scoreColor = Utils.rgb(203, 241, 194);

	init {
		// 단색용 텍스처 생성
		Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
			setColor(Color.WHITE);
			fill();
			solidColor = Texture(this);
			dispose();
		};

		// 미터기 추가
		addWidget("hp_indicator", ProgressBar({ 9f }, { Window.height - 38f }, { 180f }, color = Utils.rgb(73, 186, 73)));
		addWidget("attack_target_hp_indicator", ProgressBar({ 210f }, { Window.height - 38f }, { 180f }, color = enemyBarColor).apply { hide() });
		addWidget("gun_ammo_indicator", ProgressBar({ Window.width - 145f }, { 10f }, { 130f }, color = Utils.rgb(15, 116, 240), style = ProgressBar.Style.CHUNKED).apply { hide() });
		addWidget("gun_cooldown_indicator", ProgressBar({ Window.width - 215f }, { 10f }, { 60f }, value=0.42f, color = Color.SCARLET).apply { hide() });

		// 일시 중지 및 게임 오버 단추
		resumeButton = Button({ Window.width * 0.5f - 195f }, { 120f }, { 120f }, caption = "Resume", skin = Textures.greenButton) {
			GameManager.resume();
		};
		replayButton = Button({ Window.width * 0.5f - 195f }, { 120f }, { 120f }, caption = "Continue", skin = Textures.greenButton) {
			restartGame();
		};
		titleButton = Button({ Window.width * 0.5f - 60f }, { 120f }, { 120f }, caption = "Back to title", color = Utils.rgb(225, 247, 231)) {
			unloadWorld(dispose = true);
			GameManager.resetAll();
			GameManager.standBy();
			game.setScreen(game.titleScreen);
		};
		quitButton = Button({ Window.width * 0.5f + 75f }, { 120f }, { 120f }, caption = "Quit", color = Utils.rgb(225, 247, 231)) {
			Gdx.app.exit();
		};

		// 제목 표시줄 정보 전환
		timerManager.register(RepeatingTimer(3f, { !GameManager.isGameOver }) {
			currentTitleInfo++;
		});
	}

	// 창 최소화(아이콘 표시) 시 자동 일시 중지
	override fun pause() {
		if(GameManager.isPlaying)
			GameManager.pause();
	}

	override fun update(delta: Float) {
		timerManager.tick(delta);

		when {
			GameManager.isPlaying	-> updatePlaying(delta);
			GameManager.isPaused	-> updatePaused();
			GameManager.isGameOver	-> updateGameOver();
		}
	}

	/**
	 * 게임 진행 중 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크
	 *
	 * @param delta 직전 프레임과의 간격 (초)
	 */
	private inline fun updatePlaying(delta: Float) {  // update에서만 한 번 쓰이기 때문에 inline이다.
		// 제목 표시줄에 통계 표시
		updateTitleBarInfo();

		super.update(delta);

		val world: World? = projectingWorld;
		if(world is SinglePlayerWorld) {
			val player = world.player;
			attackTarget = player.latestAttackVictim?.takeIf { isValidAttackTarget(it, player) }
				?: run {
					val distanceSorted = Pools.entityArray.obtain();
					world.entities.getDistanceSorted(player, distanceSorted);
					val ret = (distanceSorted.firstOrNull { it is LivingEntity && it !is Bullet && it !== player } as? LivingEntity)?.takeIf { isValidAttackTarget(it, player) };
					Pools.entityArray.free(distanceSorted);

					/* return */ ret
				};
		}

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
		if(world == null) {
			Window.titleBarStats = null;
			return;
		}

		Window.titleBarStats = when(TitleInfoType.byIndex(currentTitleInfo)) {
			TitleInfoType.OPENED	-> "Opened chests: ${Statistics.openedContainerCount}"
			TitleInfoType.KILLED	-> "Killed zombies: ${Statistics.killedZombieCount}"
			TitleInfoType.FIRED		-> "Fired: ${Statistics.fireCount}"
			TitleInfoType.SURVIVED	-> "Survived duration: ${Utils.parseSeconds(Statistics.survivedDuration, "m", "s")}"
			TitleInfoType.DAMAGE	-> "Total damage: ${Statistics.totalDamage}"
			TitleInfoType.ZOMBIES	-> "Current zombies: ${world.entities.countOf<Zombie>()}"
		};
	}

	/**
	 * 미터기 정보를 갱신한다.
	 */
	private inline fun updateProgressBars() {  // updateInPlay에서만 한 번 쓰이기 때문에 inline이다.
		val hpIndicator = getWidget("hp_indicator") as ProgressBar;
		val targetIndicator = getWidget("attack_target_hp_indicator") as ProgressBar;
		val ammoIndicator = getWidget("gun_ammo_indicator") as ProgressBar;
		val cooldownIndicator = getWidget("gun_cooldown_indicator") as ProgressBar;

		val world: World? = projectingWorld;
		if(world !is SinglePlayerWorld) {
			hpIndicator.hide();
			targetIndicator.hide();
			ammoIndicator.hide();
			cooldownIndicator.hide();
			return;
		}
		val player = world.player;

		// 체력 미터기 처리
		hpIndicator.apply {
			value = player.health.toFloat() / player.maxHealth;
			show();
		};

		targetIndicator.apply {
			val target = attackTarget;
			if(target != null) {
				color = if(target.isSameTeamWith(player)) friendBarColor else enemyBarColor;
				value = target.health.toFloat() / target.maxHealth;
				show();
			} else {
				hide();
			}
		};

		// 총 관련 미터기 처리
		val holding: Item? = player.selectedItem;
		if(holding != null && holding is Gun) {
			// 총의 ammo를 미터기로 표시
			ammoIndicator.apply {
				if(!holding.infiniteBullets) {
					value = holding.remainingBullets.toFloat() / holding.maxBullets;
					show();
				} else {
					hide();
				}
			};

			// 총의 공격 쿨타임 표시
			if(holding.fireInterval > 0.2f) {
				val cooldown = holding.remainingCooldownPercentage;
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

	private inline fun isValidAttackTarget(target: LivingEntity?, player: Player) = target != null && !target.isInvincible && target.isAlive && target.distanceTo(player) <= 456f;

	/**
	 * 일시 정지 상태에서 매 프레임 로직
	 *
	 * 객체 업데이트(super.update)나 타이머(spawner.tick)를 호출하지 않음.
	 *   세상이 그대로 멈춰있는 상태가 됨.
	 */
	private inline fun updatePaused() {  // update에서만 한 번 쓰이기 때문에 inline이다.
		// 제목 표시줄에 통계 표시
		updateTitleBarInfo();
		
		// 일시 정지 키 누름 감지
		detectPauseKey();
	}

	/**
	 * 게임 오버 상태에서 매 프레임 처리
	 */
	private inline fun updateGameOver() {  // update에서만 한 번 쓰이기 때문에 inline이다.
		// ESC 키가 '막 눌린 순간' 앱 종료.
		//   isKeyJustPressed로 한 이유: 누르고 있는 동안 매 프레임 exit이 호출되지 않게.
		if(Input.isKeyJustPressed(Input.ESCAPE))
			Gdx.app.exit();

		// R 키나 사이띄개를 누르면 다시 시작
		if(Input.isKeyJustPressed(Input.R) || Input.isKeyJustPressed(Input.SPACE))
			restartGame();
	}

	/**
	 * 다음 라운드로 게임을 재시작한다.
	 */
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
	 * 일시 정지 시나 게임 오버 시 메시지와 컨트롤 표시
	 */
	override fun drawOverlay() {
		// 일시 정지 시 어둡게 변경
		if(!GameManager.isPlaying)
			drawFrozenOverlay();

		// ── 상태별로 그리는 것이 다름 ──
		when {
			// 플레이 중에는 추가로 그릴 것 없음
			GameManager.isPaused	-> drawPausedMessage();  // 일시정지 화면 그리기
			GameManager.isGameOver	-> drawGameOverMessage();
		}
	}

	/**
	 * 화면에 어두운 오버레이(주로 모달용)를 만든다.
	 */
	private inline fun drawFrozenOverlay() {  // render에서만 한 번 쓰이기 때문에 inline이다.
		batch.color = frozenOverlay;
		batch.draw(solidColor, 0f, 0f, Window.width, Window.height);
		batch.color = Color.WHITE;
	}

	/**
	 * 일시 정지 시 띄우는 메시지를 그린다.
	 */
	private inline fun drawPausedMessage() {  // render에서만 한 번 쓰이기 때문에 inline이다.
		drawText(
			text = "PAUSED",
			x = 0f,
			y = Window.height * 0.5f + 20f,
			color = Color.YELLOW,
			scale = 2.0f,
			width = Window.width,
			align = Align.center
		);
		drawText(
			text = "Press <P> or <Esc> or <Space> to resume",
			x = 0f,
			y = Window.height * 0.5f - 20f,
			color = Color.WHITE,
			scale = 1.0f,
			width = Window.width,
			align = Align.center
		);

		drawWidget(resumeButton);
		drawWidget(titleButton);
		drawWidget(quitButton);
	}

	/**
	 * 게임 오버 시 화면 중앙에 띄우는 안내 메시지를 그린다.
	 */
	private inline fun drawGameOverMessage() {  // render에서만 한 번 쓰이기 때문에 inline이다.
		drawText(
			text = "YOU DIED!",
			x = 0f,
			y = Window.height * 0.5f + 40f,
			color = Color.RED,
			scale = 2.0f,
			width = Window.width,
			align = Align.center
		);
		drawText(
			text = "Press <Esc> to exit or press <R> or <Space> to continue",
			x = 0f,
			y = Window.height * 0.5f + 10f,
			color = Color.WHITE,
			scale = 1.0f,
			width = Window.width,
			align = Align.center
		);

		// 통계
		drawText(
			text = "Opened containers: ${Statistics.openedContainerCount}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 20f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f
		);
		drawText(
			text = "Killed zombies: ${Statistics.killedZombieCount}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 35f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f
		);
		drawText(
			text = "Fired: ${Statistics.fireCount}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 50f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f
		);
		drawText(
			text = "Survived duration: ${Utils.parseSeconds(Statistics.survivedDuration, "m", "s")}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 65f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f
		);
		drawText(
			text = "Total damage: ${Statistics.totalDamage}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 80f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f
		);
		drawText(
			text = "Final score: ${ScoreManager.score}",
			x = Window.width * 0.5f - 70f,
			y = Window.height * 0.5f - 95f,
			color = Color.LIGHT_GRAY,
			scale = 1.0f
		);

		// 게임 오버 관련 단추 그리기(게임 오버 화면에서만 보임)
		drawWidget(replayButton);
		drawWidget(titleButton);
		drawWidget(quitButton);
	}

	// 로딩된 월드가 없을 때 placeholder 배경
	override fun drawBackground() {
		if(projectingWorld != null) return;

		// 표시할 월드가 없을 때 보일 placeholder (일반적으로 볼 일은 없다.)
		batch.color = noWorldOverlay;
		batch.draw(lazyStillCut.value, 0f, 0f, Window.width, Window.height);
		batch.color = Color.WHITE;
		drawText("Waiting for a new adventure...", 0f, Window.height * 0.5f, Color.LIGHT_GRAY, 2.0f, Window.width, Align.center);
	}

	override fun drawElements() {
		// 월드 및 자막 그리기
		super.drawElements();

		// 자막이 있으면 표시
		if(subtitlesVisible)
			drawText(
				text = subtitlesMessage,
				x = 0f,
				y = 20f,
				color = subtitlesColor,
				scale = 1.0f,
				width = Window.width,
				align = Align.center
			);

		// ── 항상 보이는 UI ──
		drawHud();
	}

	override fun drawSubtitles(message: String, duration: Float, color: Color) {
		subtitlesTimer?.let {
			timerManager.unregister(it);
			subtitlesTimer = null;
		};
		subtitlesMessage = message;
		subtitlesColor = color;
		subtitlesTimer = Timer(duration, { GameManager.isPlaying }) {
			subtitlesMessage = "";
			subtitlesColor = Color.WHITE;  // 초깃값으로 복원하여 메모리를 점유하지 않게 함
			subtitlesTimer = null;
		}.also { timerManager.register(it) };
	}

	/**
	 * 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지를 그린다.
	 */
	private inline fun drawHud() {  // drawElements에서만 한 번 쓰이기 때문에 inline이다.
		val world: World? = projectingWorld;
		if(world == null) return;

		if(world is SinglePlayerWorld) {
			val player = world.player;
			// 1) UI 텍스트 (화면 고정) — 좌측 상단 HP 표시.
			//    카메라가 움직여도 항상 이 위치에 있다.
			drawText(
				text = "${player.name}  [ ${player.health} ]",
				x = 10f,
				y = Window.height - 8f,   // 화면 y 축은 위로 증가 → 맨 위가 screenHeight
				color = playerNameColor,
				scale = 1.0f
			);

			// 현재 플레이어가 들고 있는 아이템
			player.selectedItem?.let {
				batch.draw(it.texture, 8f, 4f, Constants.ITEM_SIZE, Constants.ITEM_SIZE);
				drawText(
					text = "${it.name} [${player.selectedItemIndex + 1}/${player.inventory.size}]",
					x = 40f,
					y = 22f,
					color = when(it.rarity) {
						Rarity.UNCOMMON	-> Color.YELLOW  // 마인크래프트 따라함
						Rarity.RARE		-> Color.MAGENTA  // 마인크래프트 따라함
						else			-> Color.WHITE
					},
					scale = 1.0f
				);
			};

			attackTarget?.let {
				drawText(
					text = "${it.name}  [ ${it.health} ]",
					x = 211f,
					y = Window.height - 8f,
					color = if(player.isSameTeamWith(it)) friendNameColor else enemyNameColor,
					scale = 1.0f
				);
			};
		}

		// 점수
		drawText(
			text = "Score: ${ScoreManager.score}",
			x = Window.width - 130f,
			y = Window.height - 10f,
			color = scoreColor,
			scale = 1.2f,
			width = 120f,
			align = Align.right
		);
	}

	override fun dispose() {
		super.dispose();
		solidColor.dispose();
		if(lazyStillCut.isInitialized())
			lazyStillCut.value.dispose();
		resumeButton.dispose();
		replayButton.dispose();
		titleButton.dispose();
		quitButton.dispose();
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

			@JvmStatic fun byIndex(index: Int) = enumEntries[index];
		}
	}
}
