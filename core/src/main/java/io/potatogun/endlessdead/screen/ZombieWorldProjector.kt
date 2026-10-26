package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener as GdxInputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.TitleBar;
import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.Landmine;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.isSameTeamWith;
import io.potatogun.endlessdead.entity.zombie.Zombie;
import io.potatogun.endlessdead.item.Cooldownable;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.LimitedUsable;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.item.Usable;
import io.potatogun.endlessdead.world.SpecialWorld;
import io.potatogun.gdxhelper.InputListeners;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.entity.isIn;
import io.potatogun.gdxhelper.entity.manager.countOf;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.screen.WorldProjector;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.Input;
import io.potatogun.gdxhelper.util.isMobile as isMobilePlatform;
import io.potatogun.gdxhelper.util.loadTexture;
import io.potatogun.gdxhelper.util.parseSeconds;
import io.potatogun.gdxhelper.util.rgb;
import io.potatogun.gdxhelper.widget.Button;
import io.potatogun.gdxhelper.widget.ProgressBar;
import io.potatogun.gdxhelper.widget.Widget;
import io.potatogun.gdxhelper.world.World;

import java.util.function.BooleanSupplier;

/**
 * 게임 목적에 맞게 HUD와 체력 정보, 총 정보 등을 표시해주는 월드 뷰어.
 *   이쪽에는 우리 게임의 목적에 맞게 HP 미터기, 일시 정지 처리, 게임 오버 화면 등이 모두 구현되어 있다.
 */
class ZombieWorldProjector : WorldProjector(font = EndlessDead.instance.resources.font), SubtitlesDrawable {
	private val game = EndlessDead.instance;
	private val noWorldOverlay = rgb(64, 64, 64);  // 원래보다 많이 어둡게
	private val frozenOverlay = rgb(0, 0, 0, 0.5f);
	private val solidColor: Texture;
	// 제목 표시줄에 표시할 정보의 인덱스
	private var currentTitleInfo = 0;
	// 로드된 월드가 없을 때 보일 placeholder 배경
	private val lzStillCut = lazy { loadTexture("title/still_cut.bmp") };
	// 타이머
	private val timers = TimerManager();
	// 자막 관련 필드들.
	private var subtitlesTimer: Timer? = null;
	override val subtitlesDuration = 3f;
	override val subtitlesColor = Color.WHITE;
	private var currentSubtitlesMessage: String = "";
	private var currentSubtitlesColor = subtitlesColor;
	@get:JvmName("isSubtitlesVisible") private val subtitlesVisible: Boolean
		inline get() = (subtitlesTimer != null);
	private var attackTarget: LivingEntity? = null;  // 매 업데이트 시 개체가 죽으면 초기화되므로 굳이 WeakReference 쓸 필요 없음
	private val enemyBarColor = rgb(205, 46, 46);
	private val friendBarColor = rgb(132, 208, 132);
	private val playerNameColor = rgb(156, 213, 155);
	private val enemyNameColor = rgb(247, 215, 215);
	private val friendNameColor = rgb(215, 247, 215);
	private val scoreColor = rgb(255, 255, 255, 0.8f);
	private val gunCooldownBarX = Widget.DimensionSupplier { Window.width - 215f };
	private val infiniteGunCooldownBarX = Widget.DimensionSupplier { Window.width - 75f };
	// 단추들
	private val resumeButton: Button;
	private val replayButton: Button;
	private val titleButton: Button;
	private val quitButton: Button;
	// 미터기들
	private val hpIndicator: ProgressBar;
	private val targetIndicator: ProgressBar;
	private val ammoIndicator: ProgressBar;
	private val cooldownIndicator: ProgressBar;
	// 화면접촉식 장치용 인터페이스
	private val isMobile = isMobilePlatform;
	/**
	 * 조이스틱 객체를 가져온다. 모바일에서만 있다.
	 *
	 * 자바에서는 getJoystick()로 가져온다.
	 */
	val joystick: Touchpad?;
	private var joystickPointer = -1;
	val shootJoystick: Touchpad?;
	private var shootJoystickPointer = -1;
	private val previousItemButton: Button?;
	private val nextItemButton: Button?;
	private val openContainerButton: Button?;
	private val useButton: Button?;

	init {
		// 단색용 텍스처 생성
		Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
			setColor(Color.WHITE);
			fill();
			solidColor = Texture(this);
			dispose();
		};

		val resources = game.resources;

		// 미터기 추가
		hpIndicator = ProgressBar({ 9f }, { Window.height - 38f }, { 180f }, skin = resources.smoothProgressBar, color = rgb(73, 186, 73)).also { addWidget(it) };
		targetIndicator = ProgressBar({ 210f }, { Window.height - 38f }, { 180f }, skin = resources.smoothProgressBar, color = enemyBarColor).apply { hide() }.also { addWidget(it) };
		ammoIndicator = ProgressBar({ Window.width - 145f }, { 10f }, { 130f }, skin = resources.chunkedProgressBar, color = rgb(15, 116, 240), style = ProgressBar.Style.CHUNKED).apply { hide() }.also { addWidget(it) };
		cooldownIndicator = ProgressBar(gunCooldownBarX, { 10f }, { 60f }, skin = resources.smoothProgressBar, color = Color.SCARLET).apply { hide() }.also { addWidget(it) };

		// 일시 중지 및 게임 오버 단추
		resumeButton = Button({ Window.width * 0.5f - 195f }, { 120f }, { 120f }, caption = "Resume", skin = resources.primaryButton) {
			game.gameManager.resume();
		}.apply { hide() }.also { addOverlayWidget(it) };
		replayButton = Button({ Window.width * 0.5f - 195f }, { 120f }, { 120f }, caption = "Continue", skin = resources.primaryButton) {
			clearSubtitles();
			disposeParentWorld();
			game.gameManager.newGame();
		}.apply { hide() }.also { addOverlayWidget(it) };
		titleButton = Button({ Window.width * 0.5f - 60f }, { 120f }, { 120f }, caption = "Back to title", skin = resources.button) {
			disposeParentWorld();
			unloadWorld(dispose = true);
			clearSubtitles();
			game.gameManager.standBy();
		}.apply { hide() }.also { addOverlayWidget(it) };
		quitButton = Button({ Window.width * 0.5f + 75f }, { 120f }, { 120f }, caption = "Quit", skin = resources.button) {
			disposeParentWorld();
			unloadWorld(dispose = true);
			Gdx.app.exit();
		}.apply { hide() }.also { addOverlayWidget(it) };

		if(java.lang.Boolean.getBoolean("zombie.instantGame")) {
			replayButton.disable();
			titleButton.disable();
		}

		// 제목 표시줄 정보 전환
		timers.register(RepeatingTimer(3f, { (game.gameManager.isPlaying || game.gameManager.isPaused) && isProjecting }) {
			currentTitleInfo++;
			if(currentTitleInfo >= TitleInfoType.size)
				currentTitleInfo = 0;
			updateTitleBarInfo();
		});

		// 오버레이 단추 표시 또는 숨기기
		game.gameManager.attachStateObserver {
			when {
				game.gameManager.isPlaying -> {
					replayButton.hide();
					resumeButton.hide();
					titleButton.hide();
					quitButton.hide();
				}
				game.gameManager.isPaused -> {
					replayButton.hide();
					resumeButton.show();
					titleButton.show();
					quitButton.show();
				}
				game.gameManager.isGameOver -> {
					TitleBar.stats = null;

					replayButton.show();
					resumeButton.hide();
					titleButton.show();
					quitButton.show();
				}
			}
		};

		if(isMobile || java.lang.Boolean.getBoolean("zombie.forceTouchControls")) {
			joystick = Touchpad(0f, Touchpad.TouchpadStyle().apply {
				background = TextureRegionDrawable(resources.textures.getShared("joystick_background"));
				knob = TextureRegionDrawable(resources.textures.getShared("joystick_knob"));
			});
			shootJoystick = Touchpad(0f, Touchpad.TouchpadStyle().apply {
				background = TextureRegionDrawable(resources.textures.getShared("joystick_background"));
				knob = TextureRegionDrawable(resources.textures.getShared("joystick_knob_secondary"));
			});
			setJoystickBounds();

			val itemSwitcherCondition = BooleanSupplier { game.gameManager.isPlaying && game.player?.let { !it.inventory.isEmpty } ?: false };
			previousItemButton = Button(8f, 2f, 28f, 28f, caption = "<", skin = resources.button) {
				game.player?.selectPreviousItem();
			}.apply {
				setVisibleCondition(itemSwitcherCondition);
			}.also { addWidget(it) };
			nextItemButton = Button(8f + 28f + 8f, 2f, 28f, 28f, caption = ">", skin = resources.button) {
				game.player?.selectNextItem();
			}.apply {
				setVisibleCondition(itemSwitcherCondition);
			}.also { addWidget(it) };
			openContainerButton = Button(8f, 72f, 72f, 32f, caption = "Open", skin = resources.button) {
				game.player?.interactContainer();
			}.apply {
				setVisibleCondition { game.gameManager.isPlaying };
			}.also { addWidget(it) };
			useButton = Button(8f, 136f, 72f, 32f, caption = "Use", skin = resources.primaryButton) {
				game.player?.let { player ->
					player.selectedItem?.let { player.useItem(it) };
				};
			}.apply {
				setVisibleCondition { game.gameManager.isPlaying && game.player?.selectedItem?.let { it is Usable && it !is Shootable } ?: false };
			}.also { addWidget(it) };
		} else {
			joystick = null;
			shootJoystick = null;

			previousItemButton = null;
			nextItemButton = null;
			openContainerButton = null;
			useButton = null;
		}
	}

	private inline fun disposeParentWorld() {  // 원래는 세 군데에 직접 작성했던 코드라 인라인이다.
		projectingWorld?.let { world ->
			if(world is SpecialWorld) {
				val mainWorld = world.mainWorld;
				if(mainWorld != null)
					game.taskQueue.post(mainWorld, WorldProjector.safeDisposeWorld);
			}
		};
	}

	override fun loadWorld(world: World, disposePrevious: Boolean) {
		super.loadWorld(world, disposePrevious);
		updateTitleBarInfo();
	}

	override fun unloadWorld(dispose: Boolean): Boolean {
		val result = super.unloadWorld(dispose);
		updateTitleBarInfo();
		clearSubtitles();
		return result;
	}

	// 창 최소화(아이콘 표시) 시 자동 일시 중지
	override fun pause() {
		if(game.gameManager.isPlaying)
			game.gameManager.pause();
	}

	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		setJoystickBounds();
	}

	override fun onTouchDrag(x: Int, y: Int, pointer: Int): Boolean {
		var processed = super.onTouchDrag(x, y, pointer);

		val x = x.toFloat();
		val y = y.toFloat();
		if(joystick != null && (joystick.isTouched || isInActorBounds(joystick, x, y)) && pointer == joystickPointer) {
			val joystickHeight = joystick.height;
			(joystick.listeners[0] as GdxInputListener).touchDragged(null, x - joystick.x, joystickHeight - y + (Window.height - joystick.y - joystickHeight), pointer);
			processed = true;
		}
		if(shootJoystick != null && (shootJoystick.isTouched || isInActorBounds(shootJoystick, x, y)) && pointer == shootJoystickPointer) {
			val joystickHeight = shootJoystick.height;
			(shootJoystick.listeners[0] as GdxInputListener).touchDragged(null, x - shootJoystick.x, joystickHeight - y + (Window.height - shootJoystick.y - joystickHeight), pointer);
			processed = true;
		}
		return processed;
	}

	override fun onTouchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
		var processed = super.onTouchDown(x, y, pointer, button);

		val x = x.toFloat();
		val y = y.toFloat();
		if(joystick != null && isInActorBounds(joystick, x, y)) {
			val joystickHeight = joystick.height;
			processed = (joystick.listeners[0] as GdxInputListener).touchDown(null, x - joystick.x, joystickHeight - y + (Window.height - joystick.y - joystickHeight), pointer, button) || processed;
			joystickPointer = pointer;
		}
		if(shootJoystick != null && isInActorBounds(shootJoystick, x, y)) {
			val joystickHeight = shootJoystick.height;
			processed = (shootJoystick.listeners[0] as GdxInputListener).touchDown(null, x - shootJoystick.x, joystickHeight - y + (Window.height - shootJoystick.y - joystickHeight), pointer, button) || processed;
			shootJoystickPointer = pointer;
		}
		return processed;
	}

	override fun onTouchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
		var processed = super.onTouchUp(x, y, pointer, button);

		val x = x.toFloat();
		val y = y.toFloat();
		if(joystick != null && pointer == joystickPointer) {
			val joystickHeight = joystick.height;
			(joystick.listeners[0] as GdxInputListener).touchUp(null, x - joystick.x, joystickHeight - y + (Window.height - joystick.y - joystickHeight), pointer, button);
			joystickPointer = -1;
			processed = true;
		}
		if(shootJoystick != null && pointer == shootJoystickPointer) {
			val joystickHeight = shootJoystick.height;
			(shootJoystick.listeners[0] as GdxInputListener).touchUp(null, x - shootJoystick.x, joystickHeight - y + (Window.height - shootJoystick.y - joystickHeight), pointer, button);
			shootJoystickPointer = -1;
			processed = true;
		}
		return processed;
	}

	private inline fun isInActorBounds(actor: Actor, mouseX: Float, mouseY: Float): Boolean {
		if(!actor.isVisible) return false;
		val mouseY = Window.height - mouseY;
		val x = actor.x;
		val y = actor.y;
		return mouseX >= x && mouseX <= x + actor.width && mouseY >= y && mouseY <= y + actor.height;
	}

	private inline fun setJoystickBounds() {
		if(joystick == null || shootJoystick == null) return;  // 둘 중 하나만 null인 경우는 없음
		val size = Window.height * 0.25f;
		val knobSize = size * 0.421875f;
		val margin = 48f;
		joystick.setBounds(Window.width - size - margin, margin, size, size);
		joystick.style.knob.let {
			it.minWidth = knobSize;
			it.minHeight = knobSize;
		};
		joystick.setDeadzone(size * 0.5f * 0.2f);
		val shootMargin = 40f;
		shootJoystick.setBounds(margin + shootMargin, margin, size, size);
		shootJoystick.style.knob.let {
			it.minWidth = knobSize;
			it.minHeight = knobSize;
		};
		shootJoystick.setDeadzone(size * 0.5f * 0.5f);
	}

	override fun update(delta: Float) {
		timers.update(delta);

		when {
			game.gameManager.isPlaying	-> updatePlaying(delta);
			game.gameManager.isPaused	-> updatePaused();
			game.gameManager.isGameOver	-> updateGameOver();
		}
	}

	/**
	 * 게임 진행 중 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크
	 *
	 * @param delta 직전 프레임과의 간격 (초)
	 */
	private inline fun updatePlaying(delta: Float) {  // update에서만 한 번 쓰이기 때문에 inline이다.
		super.update(delta);

		shootJoystick?.setVisible(game.player?.selectedItem is Shootable);

		val player = game.player;
		val world = player?.world;
		if(world != null && isProjecting(world))
			attackTarget = player.lastAttackVictim?.takeIf { isValidAttackTarget(it, player) } ?: run { world.entities.getClosestOf<LivingEntity>(player) { isValidAttackTarget(it, player) } };

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
		val player = game.player;
		if(world == null || player == null || !player.isIn(world)) {
			TitleBar.stats = null;
			return;
		}

		val currentTitleType = TitleInfoType.byIndex(currentTitleInfo);
		TitleBar.stats =
			if(currentTitleType == TitleInfoType.OPENED)
				"Opened chests: ${player.statistics.openedContainerCount}"
			else if(currentTitleType == TitleInfoType.KILLED)
				"Killed zombies: ${player.statistics.killedZombieCount}"
			else if(currentTitleType == TitleInfoType.FIRED)
				"Fired: ${player.statistics.fireCount}"
			else if(currentTitleType == TitleInfoType.SURVIVED)
				"Survived duration: ${parseSeconds(player.statistics.survivedDuration, "m", "s")}"
			else if(currentTitleType == TitleInfoType.DAMAGE)
				"Total damage: ${player.statistics.totalDamage}"
			else if(currentTitleType == TitleInfoType.ZOMBIES)
				"Current zombies: ${world.entities.countOf<Zombie>()}"
			else
				null;
	}

	/**
	 * 미터기 정보를 갱신한다.
	 */
	private inline fun updateProgressBars() {  // updatePlaying에서만 한 번 쓰이기 때문에 inline이다.
		if(projectingWorld == null) {
			hpIndicator.hide();
			targetIndicator.hide();
			ammoIndicator.hide();
			cooldownIndicator.hide();
			return;
		}

		val player = game.player;
		if(player != null) {
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
			if(holding is LimitedUsable) {
				val remaining = holding.remainingUses;
				val maxUses = holding.maxUses;
				// 총의 ammo를 미터기로 표시
				ammoIndicator.apply {
					if(maxUses > 0) {
						value = remaining.toFloat() / holding.maxUses;
						show();
					} else {
						hide();
					}
				};
			} else {
				ammoIndicator.hide();
			}

			if(holding is Cooldownable) {
				// 총의 공격 쿨타임 표시
				if(holding.interval > 0.2f) {
					val cooldown = holding.remainingCooldown;
					val interval = holding.interval;
					if(interval > 0f && cooldown > 0f)
						cooldownIndicator.apply {
							value = cooldown / interval;
							if(holding is LimitedUsable && holding.maxUses > 0)
								cooldownIndicator.setX(gunCooldownBarX);
							else
								cooldownIndicator.setX(infiniteGunCooldownBarX);
							show();
						};
					else
						cooldownIndicator.hide();
				} else {
					cooldownIndicator.hide();
				}
			} else {
				cooldownIndicator.hide();
			}
		} else {
			hpIndicator.hide();
			targetIndicator.hide();
			ammoIndicator.hide();
			cooldownIndicator.hide();
		}
	}

	private inline fun isValidAttackTarget(target: LivingEntity?, player: Player) = target != null && !target.isInvincible && target !is Bullet && target !is Landmine && target.isAlive && target.distanceTo(player) <= 456f;

	/**
	 * 일시 정지 상태에서 매 프레임 로직
	 *
	 * 객체 업데이트(super.update)를 호출하지 않음.
	 *   세상이 그대로 멈춰있는 상태가 됨.
	 */
	private inline fun updatePaused() {  // update에서만 한 번 쓰이기 때문에 inline이다.
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
		if(Input.isKeyJustPressed(Input.R) || Input.isKeyJustPressed(Input.SPACE)) {
			clearSubtitles();
			game.gameManager.newGame();
		}
	}

	/**
	 * <Esc>나 P 글쇠가 눌렸을 때를 감지해서 일시 정지하거나 계속한다.
	 */
	private fun detectPauseKey() {
		// P키를 누르면 일시 정지 <-> 게임 진행 중 상태 토글!
		val resumeKeyPressed = Input.isKeyJustPressed(Input.SPACE);
		if(Input.isKeyJustPressed(Input.P) || Input.isKeyJustPressed(Input.ESCAPE) || resumeKeyPressed) {
			if(game.gameManager.isPlaying && !resumeKeyPressed)
				game.gameManager.pause();
			else if(game.gameManager.isPaused)
				game.gameManager.resume();
		}
	}

	/**
	 * 일시 정지 시나 게임 오버 시 메시지와 컨트롤 표시
	 */
	override fun drawOverlay() {
		// 일시 정지 시 어둡게 변경
		if(!game.gameManager.isPlaying)
			drawFrozenOverlay();

		// ── 상태별로 그리는 것이 다름 ──
		when {
			// 플레이 중에는 추가로 그릴 것 없음
			game.gameManager.isPaused	-> drawPausedMessage();  // 일시정지 화면 그리기
			game.gameManager.isGameOver	-> drawGameOverMessage();
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
		val player = game.player;
		if(player != null) {
			drawText(
				text = "Opened containers: ${player.statistics.openedContainerCount}",
				x = Window.width * 0.5f - 70f,
				y = Window.height * 0.5f - 20f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f
			);
			drawText(
				text = "Killed zombies: ${player.statistics.killedZombieCount}",
				x = Window.width * 0.5f - 70f,
				y = Window.height * 0.5f - 35f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f
			);
			drawText(
				text = "Fired: ${player.statistics.fireCount}",
				x = Window.width * 0.5f - 70f,
				y = Window.height * 0.5f - 50f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f
			);
			drawText(
				text = "Survived duration: ${parseSeconds(player.statistics.survivedDuration, "m", "s")}",
				x = Window.width * 0.5f - 70f,
				y = Window.height * 0.5f - 65f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f
			);
			drawText(
				text = "Total damage: ${player.statistics.totalDamage}",
				x = Window.width * 0.5f - 70f,
				y = Window.height * 0.5f - 80f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f
			);
			drawText(
				text = "Final score: ${player.scoreManager.score}",
				x = Window.width * 0.5f - 70f,
				y = Window.height * 0.5f - 95f,
				color = Color.LIGHT_GRAY,
				scale = 1.0f
			);
		}
	}

	// 로딩된 월드가 없을 때 placeholder 배경
	override fun drawBackground() {
		if(projectingWorld != null) return;

		// 표시할 월드가 없을 때 보일 placeholder (일반적으로 볼 일은 없다.)
		batch.color = noWorldOverlay;
		batch.draw(lzStillCut.value, 0f, 0f, Window.width, Window.height);
		batch.color = Color.WHITE;
	}

	override fun drawContents() {
		// 월드 및 자막 그리기
		super.drawContents();

		if(game.gameManager.isPlaying) {
			joystick?.draw(batch, 1f);
			if(shootJoystick?.isVisible ?: false)
				shootJoystick.draw(batch, 1f);
		}

		// 자막이 있으면 표시
		if(subtitlesVisible)
			drawText(
				text = currentSubtitlesMessage,
				x = 0f,
				y = 20f,
				color = currentSubtitlesColor,
				scale = 1.0f,
				width = Window.width,
				align = Align.center
			);

		// ── 항상 보이는 UI ──
		drawHud();
	}

	override fun drawSubtitles(message: String, duration: Float, color: Color) {
		subtitlesTimer?.let {
			timers.unregister(it);
			subtitlesTimer = null;
		};
		currentSubtitlesMessage = message;
		currentSubtitlesColor = color;
		subtitlesTimer = Timer(duration, { EndlessDead.instance.gameManager.isPlaying }) {
			currentSubtitlesMessage = "";
			currentSubtitlesColor = Color.WHITE;  // 초깃값으로 복원하여 메모리를 점유하지 않게 함
			subtitlesTimer = null;
		}.also { timers.register(it) };
	}

	// 간단한 함수라 인라인
	private inline fun clearSubtitles() {
		subtitlesTimer?.let {
			timers.unregister(it);
			subtitlesTimer = null;
		};
	}

	/**
	 * 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지를 그린다.
	 */
	private inline fun drawHud() {  // drawContents에서만 한 번 쓰이기 때문에 inline이다.
		if(!isProjecting) return;
		val player = game.player;
		if(player != null) {
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
				val rarity = it.rarity;
				val leftMargin = if(previousItemButton?.isVisible ?: false) 72f else 0f;
				batch.draw(it.texture, leftMargin + 8f, 4f, Item.sideLength, Item.sideLength);
				drawText(
					text = "${it.name} [${player.selectedItemIndex + 1}/${player.inventory.size}]",
					x = leftMargin + 40f,
					y = 22f,
					color =
						if(rarity == Rarity.UNCOMMON)
							Color.YELLOW  // 마인크래프트 따라함
						else if(rarity == Rarity.RARE)
							Color.MAGENTA
						else
							Color.WHITE,
					scale = 1f
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

			// 점수
			drawText(
				text = "Score: ${player.scoreManager.score}",
				x = Window.width - 130f,
				y = Window.height - 10f,
				color = scoreColor,
				scale = 1.2f,
				width = 120f,
				align = Align.right
			);
		}
	}

	override fun dispose() {
		super.dispose();
		solidColor.dispose();
		if(lzStillCut.isInitialized())
			lzStillCut.value.dispose();
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
			@JvmField val cachedEntries = TitleInfoType.entries;
			@JvmField val size = cachedEntries.size;

			@JvmStatic inline fun byIndex(index: Int) = cachedEntries[index];
		}
	}
}
