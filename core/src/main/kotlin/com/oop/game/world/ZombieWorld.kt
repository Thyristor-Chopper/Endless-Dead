package com.oop.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import com.oop.game.Constants;
import com.oop.game.GameManager;
import com.oop.game.GameState;
import com.oop.game.InputHandler;
import com.oop.game.ZombieGame;
import com.oop.game.ScoreManager;
import com.oop.game.Timer;
import com.oop.game.Utils;
import com.oop.game.entity.Entity;
import com.oop.game.entity.Player;
import com.oop.game.entity.Zombie;
import com.oop.game.entity.container.Building;
import com.oop.game.entity.container.Chest;
import com.oop.game.entity.container.Container;
import com.oop.game.item.Item;
import com.oop.game.item.Bandage;
import com.oop.game.item.Gun;
import com.oop.game.item.MachineGun;
import com.oop.game.item.Shoes;
import com.oop.game.item.Shotgun;
import com.oop.game.item.Timestopper
import com.oop.game.spawner.Spawner;
import com.oop.game.spawner.ZombieSpawner;
import com.oop.game.widget.ProgressBar;
import com.oop.game.widget.style.ProgressBarStyle;

import kotlin.math.floor;
import kotlin.random.Random;

/**
 * ════════════════════════════════════════════════════════════
 *  게임 월드 예제 — Player vs Enemy 회피 게임 (이미지 사용).
 * ════════════════════════════════════════════════════════════
 *
 *  GameWorld 를 상속해 만든 가장 작은 플레이 가능한 예제.
 *  학생은 이 파일을 참고해서 자기만의 월드를 만들면 된다.
 *
 *  ── 조작법 ──
 *   ▸ 화살표 키  : 플레이어 이동
 *   ▸ WASD      : 카메라 이동 (월드가 화면보다 커서 탐험 가능)
 *   ▸ ESC       : 게임 오버 후 종료
 *
 *  ── 사용 이미지 (core/src/main/resources/) ──
 *   ▸ player.png  — 30x30 플레이어 스프라이트
 *   ▸ enemy.png   — 40x40 적 스프라이트
 *   ▸ tile.png    — 64x64 흰색 정사각형 (체스판 배경에 색만 입혀 사용)
 *
 *  ── 게임 상태 ──
 *   IN_PLAY   : 일반 진행 (이동·충돌 체크)
 *   GAME_OVER : 충돌 후 정지, ESC 입력 대기
 *
 *  ── 텍스트 데모 ──
 *   ▸ 좌측 상단 "HP: 3"       — 화면 좌표 (카메라 움직여도 고정)
 *   ▸ 월드 중앙 "WORLD CENTER" — 월드 좌표 (카메라와 함께 이동)
 *   두 개를 같이 두어, 두 좌표계의 차이를 눈으로 확인할 수 있게 했다.
 *
 *  ── 배경 ──
 *   tile.png(흰 사각형)를 두 가지 색으로 틴트해 체스판처럼 깐다.
 *   카메라 이동을 눈으로 보여주기 위함이다.
 *   GameWorld.drawBackground(batch) 를 override 해서 그린다.
 *
 * @param screenWidth  화면에 보이는 영역 너비
 * @param screenHeight 화면에 보이는 영역 높이
 * @param worldWidth   월드 전체 너비 (화면보다 크면 WASD 로 탐험 가능)
 * @param worldHeight  월드 전체 높이
 */
class ZombieWorld(game: ZombieGame, width: Float = Constants.WORLD_WIDTH.toFloat(), height: Float = Constants.WORLD_HEIGHT.toFloat()) : World(game, width, height) {
	/**
	 * 제목 표시줄에 표시할 정보 종류를 담는 enumeration
	 */
	private enum class TitleInfoType {
		OPENED,
		KILLED,
		FIRED,
		SURVIVED,
		DAMAGE;
		
		companion object {
			private val enumEntries = TitleInfoType.entries;
			val size = enumEntries.size;
		
			fun byIndex(index: Int) = enumEntries[index];
		}
	}
	
    // 플레이어 — 월드 중앙 하단에서 시작.
    //   월드 크기를 함께 넘겨서, 경계 밖으로 못 나가게 한다.
    override val player = Player(this, x = width / 2, y = height / 2);
    // 좀비들만 따로 모아두는 관리용 리스트
    val zombies: List<Zombie>
		inline get() = getEntities().filterIsInstance<Zombie>();
	private val spawners = mutableListOf<Spawner>();
    // ── 체스판 배경 설정 (drawBackground() 에서 사용) ──
    //   이게 없으면 검은 배경뿐이라 카메라(WASD) 이동이 눈에 안 보인다.
    //   학생은 자기 게임에선 다른 배경을 그리거나, 그냥 두면 검은 배경이다.
    //
    //   tile.png 는 흰색 64x64 정사각형 한 장. 같은 텍스처에 batch.color 를
    //   바꿔가며 두 가지 색으로 그리는 트릭(틴트) 으로 체스판을 만든다.
    private val tileTexture = Texture(Gdx.files.internal("tile.bmp"));
    private val bgColorDark = Color(0.15f, 0.36f, 0.15f, 1f);
    private val bgColorLight = Color(0.15f, 0.42f, 0.15f, 1f);
    private val tileSize = 64f;
	// 제목 표시줄에 표시할 정보의 인덱스
	private var currentTitleInfo = 0
		set(value) {
			if(value >= TitleInfoType.size) field = 0;
			else if(value < 0) field = TitleInfoType.size - 1;
			else field = value;
		};
	private val timers = mutableListOf<Timer>();
    private val frozenOverlay = Color(0f, 0f, 0f, 0.5f);
	private val solidColor: Texture;

    /**
     * 생성자 본문 — 월드에 플레이어와 적을 등록한다.
     *   이렇게 등록해야 update / draw 루프에 포함된다.
     */
    init {
		val solidColorPixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
			setColor(Color.WHITE);
			fill();
		};
		solidColor = Texture(solidColorPixmap);
		solidColorPixmap.dispose();
		
		for(i in 0 until Random.nextInt(50) + 50) {  // 50~100개의 건물과 상자를 무작위로 배치
			val x = Random.nextInt(this.width.toInt()).toFloat();
			val y = Random.nextInt(this.height.toInt()).toFloat();
			val item: Item = generateRandomItem();  // 들어있을 아이템
			addEntity(when(Random.nextInt(2)) {
				0		-> Building(this, x, y, item);
				else	-> Chest(this, x, y, item);
			});
		}
        addEntity(player);
		
		addWidget("hp_indicator", ProgressBar({ 80f }, { game.screenHeight - 24f }, 220f, color = Color.YELLOW));
		addWidget("gun_ammo_indicator", ProgressBar({ game.screenWidth - 145f }, { 10f }, 130f, color = Color.ROYAL).apply { hide() });
		addWidget("gun_cooldown_indicator", ProgressBar({ game.screenWidth - 215f }, { 10f }, 60f, value=0.42f, color = Color.SCARLET, style = ProgressBarStyle.SMOOTH).apply { hide() });
		
		// 스포너들
		spawners.add(ZombieSpawner(this, 3f));
		
		// 10초마다 빈 상자 하나 리필
		timers.add(Timer(10f) {
			for(entity in getEntities().shuffled())
				if(entity is Container && entity.isEmpty) {
					entity.putItem(generateRandomItem());
					break;
				}
		}.register());
		
		// 제목 표시줄 정보 전환
		timers.add(Timer(3f, false) {
			currentTitleInfo++;
		}.register());
    }
	
	/**
	 * 상자에 들어갈 수 있는 아이템을 무작위로 생성한다
	 */
	private inline fun generateRandomItem(): Item {
		return when(Random.nextInt(5)) {
			0		-> MachineGun(this)
			1		-> Shotgun(this)
			2		-> Bandage(this)
			3		-> Timestopper(this)
			else	-> Shoes(this)
		};
	}

    /**
     * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경' 은 이 안에서.
     *
     * 상태별로 해야 할 일이 완전히 다르므로 when 으로 분기한다.
     * (입력 처리가 render() 가 아닌 update() 에 있는 이유:
     *  '로직과 그리기의 분리' — render 는 매 프레임 그리는 일에만 집중하고,
     *  상태 변화·입력은 update 가 책임진다.)
     */
    override fun update(delta: Float) {
		// 제목 표시줄에 정보 표시
		updateTitleBarInfo();
		
		// 미터기 정보 갱신
		updateProgressBars();
		
        when(GameManager.state) {
            GameState.IN_PLAY	-> updateInPlay(delta);
            GameState.PAUSED    -> updatePaused();
            GameState.GAME_OVER	-> updateGameOver();
        }
    }
	
	private fun togglePaused() {
		// P키를 누르면 IN_PLAY <-> PAUSED 상태 토글!
        if(InputHandler.isKeyJustPressed(InputHandler.P) || InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            if(GameManager.state == GameState.IN_PLAY) {
				Gdx.graphics.setForegroundFPS(10);  // 10fps로 제한하여 비디오 카드 리소스를 낭비하지 않게 한다
				GameManager.state = GameState.PAUSED;
			} else if(GameManager.state == GameState.PAUSED) {
				Gdx.graphics.setForegroundFPS(Constants.FPS);
				GameManager.state = GameState.IN_PLAY;
			}
        }
	}
	
	private inline fun updatePaused() {
        // 객체 업데이트(super.update)나 타이머(spawner.tick)를 호출하지 않음.
        //  세상이 그대로 멈춰있는 상태가 됨
		togglePaused();
    }
	
	private inline fun updateProgressBars() {
		// HP 미터기 처리
		val hpIndicator = getWidget("hp_indicator") as ProgressBar;
		hpIndicator.value = player.hp.toFloat() / player.maxHp;
		
		// 총 관련 미터기 처리
		val ammoIndicator = getWidget("gun_ammo_indicator") as ProgressBar;
		val cooldownIndicator = getWidget("gun_cooldown_indicator") as ProgressBar;
		val holding: Item? = player.selectedItem;
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
	 * 창 제목에 정보를 표시한다.
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
	private inline fun updateTitleBarInfo() {
		game.setTitleBarInfo(when(TitleInfoType.byIndex(currentTitleInfo)) {
			TitleInfoType.OPENED	-> "연 상자: ${player.openedContainerCount}개"
			TitleInfoType.KILLED	-> "잡은 좀비 수: ${player.killedZombieCount}"
			TitleInfoType.FIRED		-> "발사한 총알 수: ${player.firedBullets}"
			TitleInfoType.SURVIVED	-> "생존 시간: ${Utils.parseSeconds(player.survivedDuration, "분", "초")}"
			TitleInfoType.DAMAGE	-> "누적 피해량: ${player.totalDamage}"
		});
	}

    /**
	 * IN_PLAY 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크.
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun updateInPlay(delta: Float) {
        // ── 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
		super.update(delta);  // updateAllObjects과 removeDead
		for(spawner in spawners)
			spawner.tick(delta);
		
        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
        offsetX = offsetX.coerceIn(0f, width - game.screenWidth);
        offsetY = offsetY.coerceIn(0f, height - game.screenHeight);

		// 피가 0 이하가 되면 진짜 게임 오버!
        if(!player.isAlive) {
            GameManager.state = GameState.GAME_OVER;
			Gdx.graphics.setForegroundFPS(10);  // 10fps로 제한하여 게임 오버 시 비디오 카드 리소스를 낭비하지 않게 한다
		}
		
		togglePaused();
    }

    /**
	 * GAME_OVER 상태에서 매 프레임 처리 — ESC 입력만 감시한다.
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
    private inline fun updateGameOver() {
        // ESC 키가 '막 눌린 순간' 앱 종료.
        //   isKeyJustPressed 로 한 이유: 누르고 있는 동안 매 프레임 exit 호출되지 않게.
        if(InputHandler.isKeyJustPressed(InputHandler.ESCAPE))
            Gdx.app.exit();

        // R 키나 스페이스바를 누르면 다시 시작
        if(InputHandler.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R) || InputHandler.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
			Gdx.graphics.setForegroundFPS(Constants.FPS);
            GameManager.state = GameState.IN_PLAY;  // 상태를 다시 플레이로 되돌리고
            game.setScreen(ZombieWorld(game));  // 월드를 아예 새로 파서 화면을 덮어씌움
			Gdx.app.postRunnable { this@ZombieWorld.dispose() };  // 추가: 메모리 누수 방지
        }
    }

    /**
     * 배경 그리기 — GameWorld.drawBackground(batch) 를 override.
     *
     * 부모가 이미 batch.begin() 을 호출한 상태에서 이 함수를 부르므로,
     * 여기선 batch.draw() 호출만 하면 된다. (begin/end 를 또 부르면 안 된다)
     *
     * 카메라(offset) 에 따라 타일 위치가 바뀌어 이동감을 준다.
     *   타일 인덱스 자체는 월드 좌표 격자에서 변하지 않지만,
     *   각 타일을 그릴 때 offset 만큼 빼서 화면 좌표로 변환한다.
     *
     * 색을 입히는 방법:
     *   batch.color 를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     *   tile.png 가 흰색이라 어떤 색이든 그대로 적용된다.
     *   끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground(batch: SpriteBatch) {
        // 현재 카메라 시작점이 속한 타일 인덱스 (여유분으로 -1)
        val startCol = floor(offsetX / tileSize).toInt() - 1;
        val startRow = floor(offsetY / tileSize).toInt() - 1;
        // 화면을 채우는 데 필요한 타일 개수 (여유분 +3)
        val cols = (game.screenWidth / tileSize).toInt() + 3;
        val rows = (game.screenHeight / tileSize).toInt() + 3;

        for(row in startRow until startRow + rows)
            for(col in startCol until startCol + cols) {
                // 행+열이 짝수면 어둡게, 홀수면 밝게 → 체스판 패턴
                batch.color = if ((row + col) % 2 == 0) bgColorDark else bgColorLight;

                // 월드 좌표의 타일 위치에서 offset 만큼 빼면 화면 좌표
                val drawX = col * tileSize - offsetX;
                val drawY = row * tileSize - offsetY;
                batch.draw(tileTexture, drawX, drawY, tileSize, tileSize);
            }

        // 배경에 입힌 색이 다음 그리기(게임 객체)에 영향을 주지 않도록 흰색으로 복원.
        batch.color = Color.WHITE;
    }

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI 를 얹는다.
     *
     * 이 함수에서는 '그리기' 만 한다. 입력 처리·상태 변경은 update() 의 책임.
     *
     * 주의: super.render(delta) 가 화면 clear + 배경 + 객체까지 그리므로,
     *       텍스트는 반드시 super 호출 **이후** 그려야 가려지지 않는다.
     */
    override fun render(delta: Float) {
        super.render(delta);

        // ── 항상 보이는 UI ──
        drawHud();
		
		// 일시 정지 시 어둡게 변경
		drawFrozenOverlay();

        // ── 상태별로 그리는 것이 다름 ──
        when(GameManager.state) {
            GameState.IN_PLAY 	-> {
                // 플레이 중에는 추가로 그릴 것 없음
            }
            GameState.PAUSED    -> drawPausedMessage(); // 💡 [추가] 일시정지 화면 그리기
            GameState.GAME_OVER	-> drawGameOverMessage();
        }
    }
	
	private inline fun drawFrozenOverlay() {
		if(GameManager.state == GameState.IN_PLAY) return;
		
		batch.begin();
		batch.color = frozenOverlay;
		batch.draw(solidColor, 0f, 0f, game.screenWidth.toFloat(), game.screenHeight.toFloat());
		batch.color = Color.WHITE;
		batch.end();
	}
	
	/**
	 * 월드 중심처럼 배경에 오버레이되는 것을 그린다
	 */
	override fun drawBackgroundOverlay() {
		// 월드 텍스트 (월드 좌표) — 월드 정중앙에 표시
        //    WASD 로 카메라를 움직이면 이 글자도 화면에서 움직인다.
        drawTextInWorld(
            text = "*",
            x = width / 2 - 24f,
            y = height / 2 + 20f,
            color = Color.FOREST,
            scale = 8.0f,
			skipBatch = true
        );
	}

    /**
	 * 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지.
	 */
    private fun drawHud() {
        // 1) UI 텍스트 (화면 고정) — 좌측 상단 HP 표시.
        //    카메라가 움직여도 항상 이 위치에 있다.
        drawTextOnScreen(
            text = "HP: ${player.hp}",
            x = 10f,
            y = game.screenHeight - 10f,   // 화면 y 축은 위로 증가 → 맨 위가 screenHeight
            color = Color.YELLOW,
            scale = 1.2f
        );
		
		// 현재 플레이어가 들고 있는 아이템
		player.selectedItem?.let {
			drawTextOnScreen(
				text = "${it.name} [${player.selectedItemIndex!! + 1}/${player.inventoryItemCount}]",
				x = 10f,
				y = 20f,
				color = Color(1.0f, 1.0f, 0.75f, 1.0f),
				scale = 1.0f
			);
		};
		
		// 점수
		drawTextOnScreen(
            text = "Score: ${ScoreManager.score}",
            x = game.screenWidth - 130f,
            y = game.screenHeight - 10f,
            color = Color.LIME,
            scale = 1.2f,
			width = 120f,
			align = Align.right
        );
    }

    /**
	 * 게임 오버 시 화면 중앙에 띄우는 안내 메시지.
	 */
    private inline fun drawGameOverMessage() {
        drawTextOnScreen(
            text = "YOU DIED!",
            x = 0f,
            y = game.screenHeight / 2f,
            color = Color.RED,
            scale = 2.0f,
			width = game.screenWidth.toFloat(),
			align = Align.center
        );
        drawTextOnScreen(
            text = "Press <Esc> to exit or press <R> or <Space> for a new game",
            x = 0f,
            y = game.screenHeight / 2f - 40f,
            color = Color.WHITE,
            scale = 1.0f,
			width = game.screenWidth.toFloat(),
			align = Align.center
        );
		
		// 통계
        drawTextOnScreen(
            text = "Opened containers: ${player.openedContainerCount}",
            x = game.screenWidth / 2f - 70f,
            y = game.screenHeight / 2f - 60f,
            color = Color.LIGHT_GRAY,
            scale = 1.0f
        );
        drawTextOnScreen(
            text = "Killed zombies: ${player.killedZombieCount}",
            x = game.screenWidth / 2f - 70f,
            y = game.screenHeight / 2f - 75f,
            color = Color.LIGHT_GRAY,
            scale = 1.0f
        );
        drawTextOnScreen(
            text = "Fired bullets: ${player.firedBullets}",
            x = game.screenWidth / 2f - 70f,
            y = game.screenHeight / 2f - 90f,
            color = Color.LIGHT_GRAY,
            scale = 1.0f
        );
        drawTextOnScreen(
            text = "Survived duration: ${Utils.parseSeconds(player.survivedDuration, "m", "s")}",
            x = game.screenWidth / 2f - 70f,
            y = game.screenHeight / 2f - 105f,
            color = Color.LIGHT_GRAY,
            scale = 1.0f
        );
        drawTextOnScreen(
            text = "Total damage: ${player.totalDamage}",
            x = game.screenWidth / 2f - 70f,
            y = game.screenHeight / 2f - 120f,
            color = Color.LIGHT_GRAY,
            scale = 1.0f
        );
    }
	
    private inline fun drawPausedMessage() {
        drawTextOnScreen(
            text = "PAUSED",
            x = 0f,
            y = game.screenHeight / 2f + 20f,
            color = Color.YELLOW,
            scale = 2.0f,
			width = game.screenWidth.toFloat(),
			align = Align.center
        );
        drawTextOnScreen(
            text = "Press <P> or <Esc> to Resume",
            x = 0f,
            y = game.screenHeight / 2f - 20f,
            color = Color.WHITE,
            scale = 1.0f,
			width = game.screenWidth.toFloat(),
			align = Align.center
        );
    }
	
    /**
	 * 화면이 닫힐 때 — 부모도 dispose 한 뒤 우리만의 자원도 해제.
	 */
    override fun dispose() {
        super.dispose();
        tileTexture.dispose();
		solidColor.dispose();
		for(timer in timers)
			timer.unregister();
    }
}
