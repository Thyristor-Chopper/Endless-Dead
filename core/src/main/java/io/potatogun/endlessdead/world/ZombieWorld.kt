package io.potatogun.endlessdead.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.IntArray;

import io.potatogun.endlessdead.ArraySuppliers;
import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.Resources;
import io.potatogun.endlessdead.entity.Portal;
import io.potatogun.endlessdead.entity.container.Building;
import io.potatogun.endlessdead.entity.container.Chest;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.entity.container.TrapChest;
import io.potatogun.endlessdead.entity.summoner.StreamZombieSummoner;
import io.potatogun.endlessdead.entity.summoner.TriggermanSummoner;
import io.potatogun.endlessdead.entity.turret.FriendlyTurret;
import io.potatogun.endlessdead.entity.turret.HostileTurret;
import io.potatogun.endlessdead.item.Bandage;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.LandminePlacer;
import io.potatogun.endlessdead.item.MachineGun;
import io.potatogun.endlessdead.item.Shotgun;
import io.potatogun.endlessdead.item.SpeedPotion;
import io.potatogun.endlessdead.item.TimeStopper;
import io.potatogun.endlessdead.item.TurretInstaller;
import io.potatogun.endlessdead.spawner.GiantZombieSpawner;
import io.potatogun.endlessdead.spawner.Spawner;
import io.potatogun.endlessdead.spawner.ZombieSpawner;
import io.potatogun.gdxhelper.UpdateListeners;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.collections.filter;
import io.potatogun.gdxhelper.collections.randomOrNull;
import io.potatogun.gdxhelper.entity.isIn;
import io.potatogun.gdxhelper.entity.manager.SpatialGrid;
import io.potatogun.gdxhelper.pools.use;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.TimerAttachable;
import io.potatogun.gdxhelper.util.loadTexture;
import io.potatogun.gdxhelper.util.min2;
import io.potatogun.gdxhelper.util.rgb;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.SimpleFreezer;
import io.potatogun.gdxhelper.world.TimedWorld;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.ceil;
import kotlin.math.floor;
import kotlin.random.Random;

/**
 * 좀비 파밍 월드
 */
class ZombieWorld : World(2048f, 2048f, SpatialGrid(256, 128f), font = Resources.font), Freezable by SimpleFreezer(), TimedWorld, TimerAttachable {
	// 등록된 스포너 목록
	private val spawners = GdxArray<Spawner>(false, 2, ArraySuppliers.spawner);
	// 체스판 배경 설정 (drawTiles에서 사용)
	private val tile = loadTexture("world/tile_grain.bmp");
	private val bgColorDark = rgb(38, 96, 38);
	private val bgColorLight = rgb(38, 104, 38);
	private val bgColorDarkOuter = rgb(32, 72, 32);
	private val bgColorLightOuter = rgb(32, 76, 32);
	private val tileSize = 64f;
	// 풀 [艸]
	private val grass = loadTexture("world/grass.bmp");
	private val grassInfo: IntArray;
	private val grassTint = rgb(255, 255, 255, 0.6f);
	// 나무
	private val tree = loadTexture("world/tree.bmp");
	private val smallTree = loadTexture("world/tree_small.bmp");
	private val treeInfo: IntArray;  // 이 월드는 한 변 크기가 2000 뿐이므로 더블 short->int(32비트)로 써도 됨, 값은 SMALL(0), NORMAL(1)
	private val treeTint = rgb(255, 255, 255, 0.85f);  // 나무를 반투명하게
	// 타이머
	private val timers = TimerManager();
	// 이 월드의 시간
	override var time = 0f
		private set;
	// 추가 월드
	private val specialWorld: World?;
	private val hasSpecialWorld: Boolean
		inline get() = (specialWorld != null);
	// 월드 내 상자 수
	private val containerCount: Int;

	// 생성자 본문 — 월드에 개체들을 등록한다. 이렇게 등록해야 update / draw 루프에 포함된다.
	init {
		// 0.5% 확률로 총잡이 월드로 가는 포탈 추가
		if(Random.nextInt(1000) + 1 <= 5) {
			specialWorld = GunnerWorld(this);
			entities.add(Portal(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f, specialWorld));
		} else {
			specialWorld = null;
		}

		// 100~150개의 건물과 상자를 무작위로 배치
		val trapChestGeneratable = (!hasSpecialWorld && Random.nextInt(1000) + 1 <= 1);  // 0.1% 확률
		val intWidth = this.width.toInt();
		val intHeight = this.height.toInt();
		containerCount = Random.nextInt(51) + 100;
		for(i in 0 until containerCount) {
			val x = Random.nextInt(intWidth).toFloat();
			val y = Random.nextInt(intHeight).toFloat();
			val item: Item = generateLoot(!hasSpecialWorld);  // 들어있을 아이템
			val rand = Random.nextInt(100) + 1;
			entities.add(when {
				rand <= 40	-> Building(this, x, y, item)  // 40% 확률
				else		-> {
					if(rand >= 98 && trapChestGeneratable)
						TrapChest(this, x, y, item)  // 3% 확률
					else
						Chest(this, x, y, item)  // 60% 확률
				}
			});
		}

		// 각각 1% 확률로 적 공격 포탑(무적)을 임의 위치에 1~3대 설치
		for(i in 1..3)
			if(Random.nextInt(100) == i * 24)
				entities.add(FriendlyTurret(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f, true));

		if(!hasSpecialWorld) {
			// 각각 2.5% 확률로 플레이어 공격 포탑(파괴 불가)을 월드의 각 모퉁이에 설치
			if(Random.nextInt(40) == 8) entities.add(HostileTurret(this, 100f, 100f, true).apply { rotate(315f) });
			if(Random.nextInt(40) == 12) entities.add(HostileTurret(this, width - 100f, 100f, true).apply { rotate(45f) });
			if(Random.nextInt(40) == 15) entities.add(HostileTurret(this, 100f, height - 100f, true).apply { rotate(225f) });
			if(Random.nextInt(40) == 21) entities.add(HostileTurret(this, width - 100f, height - 100f, true).apply { rotate(135f) });

			// 각각 0.05% 확률로 플레이어 공격 포탑(공격, 파괴 가능)을 임의 위치에 1~3대 설치
			for(i in 1..3)
				if(Random.nextInt(10000) + 1 <= 5)
					entities.add(HostileTurret(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f));

			// 5% 확률로 총 쏘는 적도 나오는 월드
			if(Random.nextInt(20) == 7)
				for(i in 1..(Random.nextInt(3) + 1))  // 무작위로 1~3개
					if(Random.nextInt(i) == 0)  // 첫째: 100%, 둘째: 50%, 셋째: 33.3%
						entities.add(TriggermanSummoner(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f));
		}

		// 0.1% 확률로 스트림약좀비생성기가 있을 수 있음
		if(Random.nextInt(1000) + 1 <= 1)
			for(i in 1..(Random.nextInt(8) + 1))  // 무작위로 1~8개
				if(Random.nextInt(2) == 0)  // 이 경우에 대해 50% 판정
					entities.add(StreamZombieSummoner(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f));

		// 스포너 등록
		spawners.add(ZombieSpawner(this));
		if(Random.nextInt(10000) + 1 <= 1)  // 0.01% 확률로 왕좀비도 나오는 월드
			spawners.add(GiantZombieSpawner(this));

		// 등록된 개체 커밋
		entities.commit();

		// 10초마다 빈 상자 하나 리필
		timers.register(RepeatingTimer(10f) {
			Pools.entityArray.use(containerCount) { emptyContainers ->
				entities.view.filter(emptyContainers) { it is Container && it.inventory.isEmpty };
				val randomContainer = emptyContainers.randomOrNull() as Container?;
				randomContainer?.putItem(generateLoot(false));
			};
		});

		// 풀 정보 등록
		val grassCount = Random.nextInt(101) + 200;  // 200~300개
		grassInfo = IntArray(false, grassCount);
		for(i in 1..grassCount) {
			val x = Random.nextInt(intWidth);
			val y = Random.nextInt(intHeight);
			grassInfo.add(Grass.hashOf(x, y));
		}

		// 나무 정보 등록
		val treeCount = Random.nextInt(41) + 80;  // 80~120개
		treeInfo = IntArray(false, treeCount);
		for(i in 1..treeCount) {
			val x = Random.nextInt(intWidth);
			val y = Random.nextInt(intHeight);
			val size = Tree.Size.cachedEntries.random();
			treeInfo.add(Tree.hashOf(x, y, size));
		}

		UpdateListeners.register(this) { GameManager.isPlaying && GameManager.player.isIn(this) };  // this 캡처 떄문에 약참조가 될 수 없어서 dispose에서 직접 확실히 해제함.
	}

	/**
	 * 상자에 들어갈 수 있는 아이템을 무작위로 생성한다.
	 *
	 * @param allowRare 희귀 아이템 생성 가능 여부
	 */
	private fun generateLoot(allowRare: Boolean = true): Item {
		val rand = Random.nextInt(1000) + 1;  // 1~1000
		return when {
			rand <= 350	-> MachineGun()			// 35% 확률
			rand <= 650	-> Shotgun()			// 30% 확률
			rand <= 850	-> Bandage()			// 20% 확률
			rand <= 900	-> SpeedPotion()		// 5% 확률
			rand <= 950	-> LandminePlacer()		// 5% 확률
			else		-> {
				if(rand >= 1000 && allowRare)
					TurretInstaller()			// 0.1% 확률
				else
					TimeStopper()				// 5% 확률
			}
		};
	}

	// TimerAttachable 구현

	override fun attachTimer(timer: Timer) {
		timers.register(timer);
	}

	override fun detachTimer(timer: Timer) {
		timers.unregister(timer);
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 로직
	// ────────────────────────────────────────────────────────

	// 개체, 스포너 및 타이머를 갱신한다.
	override fun update(delta: Float) {
		time += delta;

		timers.update(delta);

		// ── 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
		super.update(delta);  // updateEntities와 removeDead

		// 스포너 갱신
		if(!isFrozen)
			for(i in 0 until spawners.size)
				spawners.items[i].update(delta);
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 그리기
	// ────────────────────────────────────────────────────────

	// 배경 그리기 - 카메라 위치에 따라 타일 위치가 바뀌어 이동감을 준다.
	override fun drawBackground() {
		drawTiles();
		drawGrass();
		drawTrees();
	}

	private inline fun drawTiles() {  // 원래는 drawBackground에 직접 작성했던 코드라 인라인이다.
		// 화면 크기 캐시 (getter 오버헤드 감소)
		val screenWidth = Window.width;
		val screenHeight = Window.height;

		// 현재 카메라 시작점이 속한 타일 인덱스
		val startCol = floor((cameraX - screenWidth * 0.5f) / tileSize).toInt();
		val startRow = floor((cameraY - screenHeight * 0.5f) / tileSize).toInt();

		// 화면을 채우는 데 필요한 타일 개수 (여유분으로 1)
		val cols = ceil(screenWidth / tileSize).toInt() + 1;
		val rows = ceil(screenHeight / tileSize).toInt() + 1;

		// 타일 그리기
		for(row in startRow until startRow + rows)
			for(col in startCol until startCol + cols) {
				val drawX = col * tileSize;
				val drawY = row * tileSize;

				// 행 + 열이 짝수면 어둡게, 홀수면 밝게 (체스판 패턴)
				if(drawX >= width || drawY >= height || drawX <= -tileSize || drawY <= -tileSize)
					batch.color = if((row + col) % 2 == 0) bgColorDarkOuter else bgColorLightOuter;
				else
					batch.color = if((row + col) % 2 == 0) bgColorDark else bgColorLight;

				batch.draw(tile, drawX, drawY, tileSize, tileSize);
			}

		// 배경에 입힌 색이 다음 그리기(게임 객체)에 영향을 주지 않도록 흰색으로 복원.
		batch.color = Color.WHITE;
	}

	private inline fun drawGrass() {
		// 화면 크기 캐시
		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		// 카메라 위치 캐시
		val offsetX = cameraX;
		val offsetY = cameraY;

		// 틴트 색 적용
		batch.color = grassTint;

		for(i in 0 until grassInfo.size) {
			val grassData = Grass(grassInfo.items[i]);
			val x = grassData.x.toFloat();
			val y = grassData.y.toFloat();
			val maxLength = 20f;  // 일단 하드코딩 - 20, 19 중 최댓값
			// 보이는 풀만 그리기
			if(x >= offsetX - halfScreenWidth - maxLength && x <= offsetX + halfScreenWidth + maxLength && y >= offsetY - halfScreenHeight - maxLength && y <= offsetY + halfScreenHeight + maxLength)
				batch.draw(grass, x, y, 20f, 19f);
		}

		// 흰색으로 복원
		batch.color = Color.WHITE;
	}

	private inline fun drawTrees() {  // 원래는 drawBackground에 직접 작성했던 코드라 인라인이다.
		// 화면 크기 캐시
		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		// 카메라 위치 캐시
		val offsetX = cameraX;
		val offsetY = cameraY;

		// 틴트 색 적용
		batch.color = treeTint;

		// 각 나무 항목 처리
		for(i in 0 until treeInfo.size) {
			val treeData = Tree(treeInfo.items[i]);
			val x = treeData.x.toFloat();
			val y = treeData.y.toFloat();
			val maxLength = 42f;  // 일단 하드코딩 - 18, 28, 24, 42 중 최댓값
			// 보이는 나무만 그리기
			if(x >= offsetX - halfScreenWidth - maxLength && x <= offsetX + halfScreenWidth + maxLength && y >= offsetY - halfScreenHeight - maxLength && y <= offsetY + halfScreenHeight + maxLength) {
				val size = treeData.size;
				if(size == Tree.Size.SMALL)
					batch.draw(smallTree, x, y, 18f, 28f);
				else
					batch.draw(tree, x, y, 24f, 42f);
			}
		}

		// 흰색으로 복원
		batch.color = Color.WHITE;
	}

	// 플레이어 위치에 따라 카메라 위치 변경
	override fun updateOffset() {
		val player = GameManager.player;
		if(player.world !== this) return;
		val halfScreenWidth = min2(Window.width * 0.5f, width * 0.5f);
		val halfScreenHeight = min2(Window.height * 0.5f, height * 0.5f);
		cameraX = player.x;
		cameraY = player.y;
	}

	// 화면이 닫힐 때 — 부모도 dispose한 뒤 우리만의 자원도 해제.
	override fun dispose() {
		super.dispose();
		tile.dispose();
		grass.dispose();
		tree.dispose();
		smallTree.dispose();
		specialWorld?.dispose();
		UpdateListeners.unregister(this);
	}

	/**
	 * 풀의 정보를 담는 비트필드 구조체
	 */
	@JvmInline private value class Grass(@JvmField val hash: Int) {
		val x: Int  // 상위 16비트
			inline get() = hash shr 16;
		val y: Int  // 하위 16비트
			inline get() = hash and 0xffff;

		companion object {
			inline fun of(x: Int, y: Int): Grass = Grass(hashOf(x, y));

			inline fun hashOf(x: Int, y: Int): Int = (x shl 16) or (y and 0xffff);
		}
	}

	/**
	 * 나무의 정보를 담는 비트필드 구조체
	 */
	@JvmInline private value class Tree(@JvmField val hash: Int) {
		val x: Int  // 상위 15비트
			inline get() = hash shr (15 + 2);
		val y: Int  // 다음 15비트
			inline get() = (hash shr 2) and 32767;
		val size: Tree.Size  // 하위 2비트
			inline get() = Tree.Size.byIndex(hash and 3);

		companion object {
			inline fun of(x: Int, y: Int, size: Tree.Size): Tree = Tree(hashOf(x, y, size));

			inline fun hashOf(x: Int, y: Int, size: Tree.Size): Int = (x shl (15 + 2)) or (y shl 2) or (size.ordinal and 3);
		}

		/**
		 * 배경의 나무 크기 열거형
		 */
		enum class Size {
			SMALL,
			NORMAL;

			companion object {
				@JvmField val cachedEntries = Tree.Size.entries;
				@JvmField val size = cachedEntries.size;

				@JvmStatic inline fun byIndex(index: Int) = cachedEntries[index];
			}
		}
	}
}
