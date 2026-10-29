package io.potatogun.endlessdead.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.ArraySuppliers;
import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Portal;
import io.potatogun.endlessdead.entity.container.Chest;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.entity.container.HardContainer;
import io.potatogun.endlessdead.entity.container.TrapChest;
import io.potatogun.endlessdead.entity.container.Vault;
import io.potatogun.endlessdead.entity.turret.FriendlyTurret;
import io.potatogun.endlessdead.entity.turret.HostileTurret;
import io.potatogun.endlessdead.item.Bandage;
import io.potatogun.endlessdead.item.GrenadeLauncher;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.MachineGun;
import io.potatogun.endlessdead.item.Shotgun;
import io.potatogun.endlessdead.item.TurretInstaller;
import io.potatogun.endlessdead.spawner.Spawner;
import io.potatogun.endlessdead.spawner.TriggermanSpawner;
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
import io.potatogun.gdxhelper.util.max2;
import io.potatogun.gdxhelper.util.min2;
import io.potatogun.gdxhelper.util.rgb;
import io.potatogun.gdxhelper.world.TimedWorld;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.ceil;
import kotlin.math.floor;
import kotlin.random.Random;

/**
 * 총잡이나 포탑 등이 메인 주인공인 월드
 */
class GunnerWorld @JvmOverloads constructor(override val mainWorld: World? = null) : World(1920f, 1920f, SpatialGrid(256, 128f), font = EndlessDead.instance.resources.font), TimedWorld, TimerAttachable, SpecialWorld {
	private val game = EndlessDead.instance;
	// 등록된 스포너 목록
	private val spawners = GdxArray<Spawner>(false, 2, ArraySuppliers.spawner);
	// 배경
	private val tile = loadTexture("world/rivets.bmp");  // 은색 리벳
	private val tileWidth = 80f;
	private val tileHeight = 40f;
	// 담장
	private val horizontalBorder = loadTexture("world/horizontal_border.bmp");
	private val verticalBorder = loadTexture("world/vertical_border.bmp");
	private val borderSize = 32f;
	private val borderThickness = 8f;
	// 무(無)의 공간
	private val void = loadTexture("world/void.bmp");
	private val voidTint = rgb(32, 40, 80);
	private val voidSize = 128f;
	// 타이머
	private val timers = TimerManager();
	// 이 월드의 시간
	override var time = 0f
		private set;
	// 월드 내 상자 수
	private val containerCount: Int;
	// 무작위값 생성기
	private val random: Random;

	init {
		// 시드 및 랜덤 객체 초기화
		val rawSeed = System.getProperty("zombie.worldSeed");
		if(rawSeed != null) {
			random = try {
				Random(rawSeed.toLong())
			} catch(e: NumberFormatException) {  // 난 Long이 박싱되는 게 stack trace가 생성되는 비용보다 더 싫음. 어차피 사용자가 제대로 값만 넘기면 발생하지도 않을 문제.
				println("warning: invalid world seed specified");
				Random.Default
			};
		} else {
			random = Random.Default;
		}

		// 30~80개의 상자를 무작위로 배치
		val intWidth = this.width.toInt();
		val intHeight = this.height.toInt();
		containerCount = random.nextInt(51) + 30;
		for(i in 0 until containerCount) {
			val x = random.nextInt(intWidth).toFloat();
			val y = random.nextInt(intHeight).toFloat();
			val rand = random.nextInt(100) + 1;
			val isVault = rand <= 35;  // 35% 확률
			val item: Item = generateLoot(isVault);  // 들어있을 아이템
			entities.add(when {
				isVault		-> Vault(this, x, y, item)			// 35% 확률
				rand >= 96	-> TrapChest(this, x, y, item)		// 5% 확률
				else		-> Chest(this, x, y, item)			// 60% 확률 (상자 전체: 65%)
			});
		}

		// 5% 확률로 적 공격 포탑(무적)을 임의 위치에 1~3대 설치
		if(random.nextInt(100) + 1 <= 5)
			for(i in 1..(random.nextInt(3) + 1))
				entities.add(FriendlyTurret(this, random.nextInt((width - 300f).toInt()).toFloat() + 150f, random.nextInt((height - 300f).toInt()).toFloat() + 150f, true));

		// 각각 50% 확률로 플레이어 공격 포탑(파괴 불가)을 월드의 각 모퉁이에 설치
		if(random.nextInt(2) == 0) entities.add(HostileTurret(this, 100f, 100f, true).apply { rotate(315f) });
		if(random.nextInt(2) == 0) entities.add(HostileTurret(this, width - 100f, 100f, true).apply { rotate(45f) });
		if(random.nextInt(2) == 0) entities.add(HostileTurret(this, 100f, height - 100f, true).apply { rotate(225f) });
		if(random.nextInt(2) == 0) entities.add(HostileTurret(this, width - 100f, height - 100f, true).apply { rotate(135f) });

		// 각각 5% 확률로 플레이어 공격 포탑(공격, 파괴 가능)을 임의 위치에 1~3대 설치
		for(i in 1..3)
			if(random.nextInt(100) + 1 <= 5)
				entities.add(HostileTurret(this, random.nextInt((width - 300f).toInt()).toFloat() + 150f, random.nextInt((height - 300f).toInt()).toFloat() + 150f));

		if(mainWorld != null)
			entities.add(Portal(this, random.nextInt((width - 300f).toInt()).toFloat() + 150f, random.nextInt((height - 300f).toInt()).toFloat() + 150f, mainWorld));

		spawners.add(TriggermanSpawner(this));

		// 등록된 개체 커밋
		entities.commit();

		// 15초마다 빈 상자 재지급
		timers.register(RepeatingTimer(15f) {
			Pools.entityArray.use(containerCount) { emptyContainers ->
				entities.view.filter(emptyContainers) { container -> container is Container && container !is HardContainer && container.isEmpty };
				val container = emptyContainers.randomOrNull() as Container?;
				container?.putItem(generateLoot());
			};
		});

		game.updateListeners.register(this) { world, game -> val player = (game as? EndlessDead)?.player; game is EndlessDead && game.gameManager.isPlaying && player != null && player.isIn(world as World) };
	}

	/**
	 * 상자에 들어갈 수 있는 아이템을 무작위로 생성한다.
	 */
	private fun generateLoot(allowRare: Boolean = false): Item {
		val rand = random.nextInt(100) + 1;  // 1~100
		return when {
			rand <= 35	-> MachineGun()			// 35% 확률
			rand <= 70	-> Shotgun()			// 35% 확률
			rand <= 90	-> GrenadeLauncher()	// 20% 확률
			else		-> {
				if(allowRare && rand > 95)
					TurretInstaller()			// 5% 확률
				else
					Bandage()					// 5%/10% 확률
			}
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

		val startCol = floor((cameraX - screenWidth * 0.5f) / tileWidth).toInt();
		val startRow = floor((cameraY - screenHeight * 0.5f) / tileHeight).toInt();

		val cols = ceil(screenWidth / tileWidth).toInt() + 1;
		val rows = ceil(screenHeight / tileHeight).toInt() + 1;

		for(row in startRow until startRow + rows)
			for(col in startCol until startCol + cols) {
				val drawX = col * tileWidth;
				val drawY = row * tileHeight;
				if(drawX < width && drawY < height && drawX > -tileWidth && drawY > -tileHeight)
					batch.draw(tile, drawX, drawY, tileWidth, tileHeight);
			}
	}

	override fun drawContents() {
		super.drawContents();
		drawVoid();
		drawBorders();
	}

	private inline fun drawVoid() {
		val screenWidth = Window.width;
		val screenHeight = Window.height;

		val startCol = floor((cameraX - screenWidth * 0.5f) / voidSize).toInt();
		val startRow = floor((cameraY - screenHeight * 0.5f) / voidSize).toInt();

		val cols = ceil(screenWidth / voidSize).toInt() + 1;
		val rows = ceil(screenHeight / voidSize).toInt() + 1;

		batch.color = voidTint;
		for(row in startRow until startRow + rows)
			for(col in startCol until startCol + cols) {
				val drawX = col * voidSize;
				val drawY = row * voidSize;
				if(drawX >= width || drawY >= height || drawX <= -voidSize || drawY <= -voidSize)
					batch.draw(void, drawX, drawY, voidSize, voidSize);
			}

		batch.color = Color.WHITE;
	}

	private inline fun drawBorders() {
		val screenWidth = Window.width;
		val screenHeight = Window.height;

		val startCol = max2(0, floor((cameraX - screenWidth * 0.5f) / borderSize).toInt());
		val startRow = max2(0, floor((cameraY - screenHeight * 0.5f) / borderSize).toInt());

		val horizontalBorderCount = ceil(screenWidth / borderSize).toInt() + 1;
		val verticalBorderCount = ceil(screenHeight / borderSize).toInt() + 1;

		val halfBorderThickness = borderThickness * 0.5f;

		for(k in 0..1) {
			val drawY = k * height - halfBorderThickness;
			for(i in startCol until startCol + horizontalBorderCount) {
				val drawX = i * borderSize;
				if(drawX >= width) break;
				batch.draw(horizontalBorder, drawX, drawY, borderSize, borderThickness);
			}

			val drawX = k * width - halfBorderThickness;
			for(i in startRow until startRow + verticalBorderCount) {
				val drawY = i * borderSize;
				if(drawY >= height) break;
				batch.draw(verticalBorder, drawX, drawY, borderThickness, borderSize);
			}
		}
	}

	override fun updateOffset() {
		val player = game.player;
		if(player == null || !player.isIn(this)) return;
		cameraX = player.x;
		cameraY = player.y;
	}

	override fun dispose() {
		super.dispose();
		tile.dispose();
		horizontalBorder.dispose();
		verticalBorder.dispose();
		void.dispose();
		game.updateListeners.unregister(this);
	}
}
