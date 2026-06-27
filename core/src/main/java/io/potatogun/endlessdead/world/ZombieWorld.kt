package io.potatogun.endlessdead.world;

import com.badlogic.gdx.graphics.Color;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.entity.FriendlyTurret;
import io.potatogun.endlessdead.entity.HostileTurret;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.container.Building;
import io.potatogun.endlessdead.entity.container.Chest;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.entity.container.TrapChest;
import io.potatogun.endlessdead.item.Bandage;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.MachineGun;
import io.potatogun.endlessdead.item.Shotgun;
import io.potatogun.endlessdead.item.SpeedPotion;
import io.potatogun.endlessdead.item.TimeStopper;
import io.potatogun.endlessdead.item.TurretInstaller;
import io.potatogun.endlessdead.spawner.Spawner;
import io.potatogun.endlessdead.spawner.ZombieSpawner;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.util.RepeatingTimer;
import io.potatogun.gdxhelper.util.Timer;
import io.potatogun.gdxhelper.util.TimerManager;
import io.potatogun.gdxhelper.util.getAllOf;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.ceil;
import kotlin.math.floor;
import kotlin.random.Random;

/**
 * ════════════════════════════════════════════════════════════
 *  게임 월드 예제 — Player vs Enemy 회피 게임 (이미지 사용).
 * ════════════════════════════════════════════════════════════
 *
 *  World를 상속해 만든 가장 작은 플레이 가능한 예제.
 *  이 파일을 참고해서 자기만의 월드를 만들면 된다.
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
 *   PLAYING   : 일반 진행 (이동·충돌 체크)
 *   PAUSED    : 일시 중지 상태
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
 *   GameWorld.drawBackground(batch)를 override 해서 그린다.
 */
class ZombieWorld : World(Constants.ZOMBIE_WORLD_WIDTH, Constants.ZOMBIE_WORLD_HEIGHT, World.Properties().tileSize(128f)), Freezable, SinglePlayerWorld {
	/**
	 * 플레이어 — 월드 중앙에서 시작.
	 */
	override val player = Player(this, width * 0.5f, height * 0.5f);
	/**
	 * 등록된 스포너
	 */
	private val spawners = mutableListOf<Spawner>();
	// ── 체스판 배경 설정 (drawBackground()에서 사용) ──
	//   이게 없으면 검은 배경뿐이라 카메라(WASD) 이동이 눈에 안 보인다.
	//   자기 게임에선 다른 배경을 그리거나, 그냥 두면 검은 배경이다.
	//
	//   tile.png는 흰색 64x64 정사각형 한 장. 같은 텍스처에 batch.color를
	//   바꿔가며 두 가지 색으로 그리는 트릭(틴트)으로 체스판을 만든다.
	private val tileTexture = Utils.loadTexture("world/tile.bmp");
	private val bgColorDark = Utils.rgb(38, 92, 38);
	private val bgColorLight = Utils.rgb(38, 107, 38);
	private val tileSize = 64f;
	override var isFrozen: Boolean = false
		private set;
	// 타이머
	private val timerManager = TimerManager();
	private var unfreezer: Timer? = null;

	/**
	 * 생성자 본문 — 월드에 플레이어와 적을 등록한다.
	 *   이렇게 등록해야 update / draw 루프에 포함된다.
	 */
	init {
		// 50~100개의 건물과 상자를 무작위로 배치
		val trapChestGeneratable = (Random.nextInt(1000) + 1 <= 5);  // 0.5%
		val intWidth = this.width.toInt();
		val intHeight = this.height.toInt();
		for(i in 0 until Random.nextInt(51) + 50) {
			val x = Random.nextInt(intWidth).toFloat();
			val y = Random.nextInt(intHeight).toFloat();
			val item: Item = generateRandomItem();  // 들어있을 아이템
			val rand = Random.nextInt(10);
			entities.add(when {
				rand <= 3	-> Building(this, x, y, item)
				else		-> {
					if(rand == 4 && trapChestGeneratable)
						TrapChest(this, x, y, item)
					else
						Chest(this, x, y, item)
				}
			});
		}

		// 각각 1% 확률로 좀비 공격 포탑을 임의 위치에 1~3대 설치
		for(i in 1..3)
			if(Random.nextInt(100) == i * 24)
				entities.add(FriendlyTurret(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f));
		// 각각 2.5% 확률로 플레이어 공격 포탑을 월드의 각 모퉁이에 설치
		if(Random.nextInt(40) == 8) entities.add(HostileTurret(this, 100f, 100f).apply { rotate(315f) });
		if(Random.nextInt(40) == 12) entities.add(HostileTurret(this, width - 100f, 100f).apply { rotate(45f) });
		if(Random.nextInt(40) == 15) entities.add(HostileTurret(this, 100f, height - 100f).apply { rotate(225f) });
		if(Random.nextInt(40) == 21) entities.add(HostileTurret(this, width - 100f, height - 100f).apply { rotate(135f) });

		// 플레이어 등록
		entities.add(player);

		// 스포너 등록
		spawners.add(ZombieSpawner(this, 3f));

		// 10초마다 빈 상자 하나 리필
		timerManager.register(RepeatingTimer(10f) {
			val emptyContainers = entities.getAllOf<Container>().filter { it.inventory.isEmpty };
			emptyContainers.randomOrNull()?.putItem(generateRandomItem());
		});
	}

	/**
	 * 상자에 들어갈 수 있는 아이템을 무작위로 생성한다.
	 */
	private fun generateRandomItem(): Item {
		val rand = Random.nextInt(1000) + 1;  // 1~1000
		return when {
			rand <= 350	-> MachineGun()			// 35%
			rand <= 650	-> Shotgun()			// 30%
			rand <= 850	-> Bandage()			// 20%
			rand <= 950	-> SpeedPotion()		// 10%
			rand <= 999	-> TimeStopper()		// 5%
			else		-> TurretInstaller()	// 0.1%
		};
	}

	// ---- Freezable 구현 -----

	override fun freeze(duration: Float, unfreezeCallback: (() -> Unit)?) {
		isFrozen = true;
		cancelUnfreezer();  // 기존 타이머 해제
		if(duration > 0f)
			unfreezer = Timer(duration) {
				unfreeze();
				unfreezeCallback?.invoke();
			}.also { timerManager.register(it) };
	}

	override fun unfreeze() {
		isFrozen = false;
		cancelUnfreezer();
	}

	private inline fun cancelUnfreezer() {
		unfreezer?.let {
			timerManager.unregister(it);
			unfreezer = null;
		};
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 로직
	// ────────────────────────────────────────────────────────

	/**
	 * 매 프레임 처리 — 개체, 스포너 및 타이머 갱신
	 */
	override fun update(delta: Float) {
		timerManager.tick(delta);

		// ── 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
		super.update(delta);  // updateEntities와 removeDead

		// 스포너 갱신
		if(!isFrozen)
			for(spawner in spawners)
				spawner.update(delta);

		// 피가 0 이하가 되면 진짜 게임 오버!
		if(!player.isAlive)
			GameManager.setGameOver();
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 그리기
	// ────────────────────────────────────────────────────────

	/**
	 * 배경 그리기
	 *
	 * 부모가 이미 batch.begin()을 호출한 상태에서 이 함수를 부르므로,
	 * 여기선 batch.draw() 호출만 하면 된다. (begin/end를 또 부르면 안 된다)
	 *
	 * 카메라(offset)에 따라 타일 위치가 바뀌어 이동감을 준다.
	 *
	 * 색을 입히는 방법:
	 *   batch.color를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
	 *   tile.png가 흰색이라 어떤 색이든 그대로 적용된다.
	 *   끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
	 */
	override fun drawBackground() {
		val screenWidth = Window.width;
		val screenHeight = Window.height;

		// 현재 카메라 시작점이 속한 타일 인덱스
		val startCol = floor((offsetX - screenWidth * 0.5f) / tileSize).toInt();
		val startRow = floor((offsetY - screenHeight * 0.5f) / tileSize).toInt();
		// 화면을 채우는 데 필요한 타일 개수 (여유분으로 1)
		val cols = ceil(screenWidth / tileSize).toInt() + 1;
		val rows = ceil(screenHeight / tileSize).toInt() + 1;

		for(row in startRow until startRow + rows)
			for(col in startCol until startCol + cols) {
				// 행+열이 짝수면 어둡게, 홀수면 밝게 → 체스판 패턴
				batch.color = if((row + col) % 2 == 0) bgColorDark else bgColorLight;

				// 월드 좌표의 타일 위치에서 offset 만큼 빼면 화면 좌표
				val drawX = col * tileSize;
				val drawY = row * tileSize;
				batch.draw(tileTexture, drawX, drawY, tileSize, tileSize);
			}

		// 배경에 입힌 색이 다음 그리기(게임 객체)에 영향을 주지 않도록 흰색으로 복원.
		batch.color = Color.WHITE;

		// 월드 텍스트 (월드 좌표) — 월드 정중앙에 표시
		//    WASD로 카메라를 움직이면 이 글자도 화면에서 움직인다.
		drawText(
			text = "*",
			x = width * 0.5f - 24f,
			y = height * 0.5f + 20f,
			color = Color.FOREST,
			scale = 8.0f
		);
	}

	// 플레이어 위치에 따라 카메라 위치 변경
	override fun updateCameraOffset() {
		// 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
		//   보여주는 영역이 [offset, offset+screen]이어야 하므로
		//   offset은 0 ~ (world - screen) 범위여야 한다.

		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		offsetX = player.x.coerceIn(halfScreenWidth, width - halfScreenWidth);
		offsetY = player.y.coerceIn(halfScreenHeight, height - halfScreenHeight);
	}

	// 화면이 닫힐 때 — 부모도 dispose한 뒤 우리만의 자원도 해제.
	override fun dispose() {
		super.dispose();
		tileTexture.dispose();
	}
}
