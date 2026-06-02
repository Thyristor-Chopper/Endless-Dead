package io.potatogun.endlessdead.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer.Task;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.Input;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.Timer;
import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.Window;
import io.potatogun.endlessdead.entity.Entity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.entity.container.Building;
import io.potatogun.endlessdead.entity.container.Chest;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Bandage;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.MachineGun;
import io.potatogun.endlessdead.item.Shotgun;
import io.potatogun.endlessdead.item.SpeedPotion;
import io.potatogun.endlessdead.item.TimeStopper;
import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.screen.WorldViewer;
import io.potatogun.endlessdead.spawner.Spawner;
import io.potatogun.endlessdead.spawner.ZombieSpawner;

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
 *   GameWorld.drawBackground(batch) 를 override 해서 그린다.
 *
 * @param width   월드 전체 너비 (화면보다 크면 WASD 로 탐험 가능)
 * @param height  월드 전체 높이
 */
class ZombieWorld(game: EndlessDead, width: Float, height: Float) : World(game, width, height), Freezable {
    // 플레이어 — 월드 중앙에서 시작.
    override val player = Player(this, Position(halfWidth, halfHeight));
	private val spawners = mutableListOf<Spawner>();
    // ── 체스판 배경 설정 (drawBackground()에서 사용) ──
    //   이게 없으면 검은 배경뿐이라 카메라(WASD) 이동이 눈에 안 보인다.
    //   자기 게임에선 다른 배경을 그리거나, 그냥 두면 검은 배경이다.
    //
    //   tile.png는 흰색 64x64 정사각형 한 장. 같은 텍스처에 batch.color를
    //   바꿔가며 두 가지 색으로 그리는 트릭(틴트)으로 체스판을 만든다.
    private val tileTexture = Textures.loadTexture("world/tile.bmp");
    private val bgColorDark = Utils.rgb(38, 92, 38);
    private val bgColorLight = Utils.rgb(38, 107, 38);
    private val tileSize = 64f;
	override var isFrozen: Boolean = false  // world의 시간이 정지되었는지 확인하는 변수
		private set;
	private var unfreezer: Task? = null;

    /**
     * 생성자 본문 — 월드에 플레이어와 적을 등록한다.
     *   이렇게 등록해야 update / draw 루프에 포함된다.
     */
    init {
		// 점수 초기화
		game.scoreManager.resetScore();

		// 50~100개의 건물과 상자를 무작위로 배치
		for(i in 0 until Random.nextInt(50) + 50) {
			val x = Random.nextInt(this.width.toInt()).toFloat();
			val y = Random.nextInt(this.height.toInt()).toFloat();
			val position = Position(x, y);
			val item: Item = generateRandomItem();  // 들어있을 아이템
			addEntity(when(Random.nextInt(2)) {
				0		-> Building(this, position, item);
				else	-> Chest(this, position, item);
			});
		}

		// 플레이어 등록
        addEntity(player);

		// 스포너 등록
		spawners.add(ZombieSpawner(this, 3f));

		// 10초마다 빈 상자 하나 리필
		timerManager.registerTimer(Timer(10f) {
			val emptyContainers = getEntities().filterIsInstance<Container>().filter { it.isEmpty };
			emptyContainers.randomOrNull()?.putItem(generateRandomItem());
		});
    }

	/**
	 * 상자에 들어갈 수 있는 아이템을 무작위로 생성한다.
	 */
	private fun generateRandomItem(): Item {
		val rand = Random.nextInt(100) + 1;  // 1~100
		return when {
			rand <= 40	-> MachineGun(this)	// 40%
			rand <= 70	-> Shotgun(this)		// 30%
			rand <= 85	-> Bandage(this)		// 15%
			rand <= 95	-> SpeedPotion(this)	// 10%
			else		-> TimeStopper(this)	// 5%
		};
	}

	// ---- Freezable 구현 -----

	override fun freeze(duration: Float) {
		isFrozen = true;
		unfreezer?.let { Utils.clearTimeout(it) };
		unfreezer = Utils.setTimeout(duration) {
			unfreeze();
			unfreezer = null;
		};
	}

	override fun unfreeze() {
		isFrozen = false;
		viewer?.drawSubtitles("Time moves again");
	}

    // ────────────────────────────────────────────────────────
    //  매 프레임 로직
    // ────────────────────────────────────────────────────────

    /**
	 * PLAYING 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크.
	 */
    override fun update(delta: Float) {
        // ── 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
		super.update(delta);  // updateAllObjects과 removeDead
		// 스포너 갱신
		if(!isFrozen)
			for(spawner in spawners)
				spawner.update(delta);

		// 피가 0 이하가 되면 진짜 게임 오버!
        if(!player.isAlive) {
            game.gameManager.setGameOver();
		}
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
     *   타일 인덱스 자체는 월드 좌표 격자에서 변하지 않지만,
     *   각 타일을 그릴 때 offset만큼 빼서 화면 좌표로 변환한다.
     *
     * 색을 입히는 방법:
     *   batch.color를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     *   tile.png가 흰색이라 어떤 색이든 그대로 적용된다.
     *   끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground() {
		// Window.width는 private set로 @JvmField가 불가능하여 내부적으로 함수 호출이 발생하여
		//   반복된 함수 호출 오버헤드를 줄이기 위해 미리 저장해둔다.

		// Window.width는 private set로 @JvmField가 불가능하여 내부적으로 함수 호출이 발생한다.
		//   이 함수 콜 오버헤드와 부동 소수점 나눗셈 연산 중 후자가 더 성능에 영향이 있다고 하여
		//   필드 접근 콜을 두 번 하는 건 그냥 넘어가자.

		// 현재 카메라 시작점이 속한 타일 인덱스
		val startCol = floor((offsetX - Window.halfWidth) / tileSize).toInt();
		val startRow = floor((offsetY - Window.halfHeight) / tileSize).toInt();
        // 화면을 채우는 데 필요한 타일 개수 (여유분으로 1)
        val cols = ceil(Window.width / tileSize).toInt() + 1;
        val rows = ceil(Window.height / tileSize).toInt() + 1;

        for(row in startRow until startRow + rows)
            for(col in startCol until startCol + cols) {
                // 행+열이 짝수면 어둡게, 홀수면 밝게 → 체스판 패턴
                batch.color = if ((row + col) % 2 == 0) bgColorDark else bgColorLight;

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
            x = halfWidth - 24f,
            y = halfHeight + 20f,
            color = Color.FOREST,
            scale = 8.0f,
			skipBatch = true
        );
    }

    /**
	 * 화면이 닫힐 때 — 부모도 dispose한 뒤 우리만의 자원도 해제.
	 */
    override fun dispose() {
        super.dispose();
        tileTexture.dispose();
		spawners.clear();
		unfreezer?.let {
			Utils.clearTimeout(it);
			unfreezer = null;
		};
    }
}
