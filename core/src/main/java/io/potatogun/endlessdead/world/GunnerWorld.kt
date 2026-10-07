package io.potatogun.endlessdead.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.ArraySuppliers;
import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Portal;
import io.potatogun.endlessdead.entity.container.Chest;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.entity.container.TrapChest;
import io.potatogun.endlessdead.entity.turret.FriendlyTurret;
import io.potatogun.endlessdead.entity.turret.HostileTurret;
import io.potatogun.endlessdead.item.Bandage;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.MachineGun;
import io.potatogun.endlessdead.item.Shotgun;
import io.potatogun.endlessdead.item.TurretInstaller;
import io.potatogun.endlessdead.spawner.Spawner;
import io.potatogun.endlessdead.spawner.TriggermanSpawner;
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
import io.potatogun.gdxhelper.world.TimedWorld;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.ceil;
import kotlin.math.floor;
import kotlin.random.Random;

/**
 * 총잡이나 포탑 등이 메인 주인공인 월드
 */
class GunnerWorld @JvmOverloads constructor(override val mainWorld: World? = null) : World(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, SpatialGrid(256, 128f)), TimedWorld, TimerAttachable, SpecialWorld {
	/**
	 * 등록된 스포너 목록
	 */
	private val spawners = GdxArray<Spawner>(false, 2, ArraySuppliers.spawner);
	private val tileTexture = loadTexture("world/rivets.bmp");
	private val bgTileWidth = 80f;
	private val bgTileHeight = 46f;
	// 타이머
	private val timers = TimerManager();
	// 이 월드의 시간
	override var time = 0f
		private set;
	private val containerCount: Int;

	init {
		// 30~80개의 상자를 무작위로 배치
		val intWidth = this.width.toInt();
		val intHeight = this.height.toInt();
		containerCount = Random.nextInt(51) + 30;
		for(i in 0 until containerCount) {
			val x = Random.nextInt(intWidth).toFloat();
			val y = Random.nextInt(intHeight).toFloat();
			val item: Item = generateLoot();  // 들어있을 아이템
			val rand = Random.nextInt(100) + 1;
			entities.add(when {
				rand >= 98	-> TrapChest(this, x, y, item)		// 3% 확률
				else		-> Chest(this, x, y, item)			// 97% 확률
			});
		}

		// 5% 확률로 적 공격 포탑(무적)을 임의 위치에 1~3대 설치
		if(Random.nextInt(100) + 1 <= 5)
			for(i in 1..(Random.nextInt(3) + 1))
				entities.add(FriendlyTurret(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f, true));

		// 각각 50% 확률로 플레이어 공격 포탑(파괴 불가)을 월드의 각 모퉁이에 설치
		if(Random.nextInt(2) == 0) entities.add(HostileTurret(this, 100f, 100f, true).apply { rotate(315f) });
		if(Random.nextInt(2) == 0) entities.add(HostileTurret(this, width - 100f, 100f, true).apply { rotate(45f) });
		if(Random.nextInt(2) == 0) entities.add(HostileTurret(this, 100f, height - 100f, true).apply { rotate(225f) });
		if(Random.nextInt(2) == 0) entities.add(HostileTurret(this, width - 100f, height - 100f, true).apply { rotate(135f) });

		// 각각 5% 확률로 플레이어 공격 포탑(공격, 파괴 가능)을 임의 위치에 1~3대 설치
		for(i in 1..3)
			if(Random.nextInt(100) + 1 <= 5)
				entities.add(HostileTurret(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f));

		if(mainWorld != null)
			entities.add(Portal(this, Random.nextInt((width - 300f).toInt()).toFloat() + 150f, Random.nextInt((height - 300f).toInt()).toFloat() + 150f, mainWorld));

		spawners.add(TriggermanSpawner(this));

		timers.register(RepeatingTimer(10f) {
			Pools.entityArray.use(containerCount) { emptyContainers ->
				entities.view.filter(emptyContainers) { it is Container && it.inventory.isEmpty };
				val randomContainer = emptyContainers.randomOrNull() as Container?;
				randomContainer?.putItem(generateLoot());
			};
		});

		UpdateListeners.register(this) { GameManager.isPlaying && GameManager.player.isIn(this) };
	}

	/**
	 * 상자에 들어갈 수 있는 아이템을 무작위로 생성한다.
	 */
	private fun generateLoot(): Item {
		val rand = Random.nextInt(100) + 1;  // 1~100
		return when {
			rand <= 40	-> MachineGun()			// 40% 확률
			rand <= 80	-> Shotgun()			// 40% 확률
			rand <= 95	-> Bandage()			// 15% 확률
			else		-> TurretInstaller()	// 5% 확률
		};
	}

	override fun attachTimer(timer: Timer) {
		timers.register(timer);
	}

	override fun detachTimer(timer: Timer) {
		timers.unregister(timer);
	}

	override fun update(delta: Float) {
		time += delta;

		timers.update(delta);

		super.update(delta);

		for(i in 0 until spawners.size)
			spawners.items[i].update(delta);
	}

	override fun drawBackground() {
		val screenWidth = Window.width;
		val screenHeight = Window.height;

		val startCol = floor((cameraX - screenWidth * 0.5f) / bgTileWidth).toInt();
		val startRow = floor((cameraY - screenHeight * 0.5f) / bgTileHeight).toInt();

		val cols = ceil(screenWidth / bgTileWidth).toInt() + 1;
		val rows = ceil(screenHeight / bgTileHeight).toInt() + 1;

		for(row in startRow until startRow + rows)
			for(col in startCol until startCol + cols) {
				val drawX = col * bgTileWidth;
				val drawY = row * bgTileHeight;
				batch.draw(tileTexture, drawX, drawY, bgTileWidth, bgTileHeight);
			}
	}

	override fun updateOffset() {
		val player = GameManager.player;
		if(player.world !== this) return;
		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		cameraX = player.x.coerceIn(halfScreenWidth, width - halfScreenWidth);
		cameraY = player.y.coerceIn(halfScreenHeight, height - halfScreenHeight);
	}

	override fun dispose() {
		super.dispose();
		tileTexture.dispose();
		UpdateListeners.unregister(this);
	}
}
