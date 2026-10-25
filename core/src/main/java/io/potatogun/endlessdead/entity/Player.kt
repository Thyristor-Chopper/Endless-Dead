package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.IdentitySet;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.StatisticsManager;
import io.potatogun.endlessdead.entity.forEachNearby;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.endlessdead.entity.component.ItemDropComponent;
import io.potatogun.endlessdead.entity.component.ItemPickupComponent;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.entity.container.HardContainer;
import io.potatogun.endlessdead.entity.listener.AttackListener;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.entity.zombie.Zombie;
import io.potatogun.endlessdead.inventory.Inventory;
import io.potatogun.endlessdead.inventory.LinearInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.item.Usable;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.WorldProjector;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.Input;
import io.potatogun.gdxhelper.util.InputListener;
import io.potatogun.gdxhelper.util.beep2;
import io.potatogun.gdxhelper.util.loadTexture;
import io.potatogun.gdxhelper.world.World;

import java.lang.ref.WeakReference;

/**
 * 플레이어 — 화살표나 WASD로 조종
 */
class Player private constructor(world: World? = null, x: Float = 0f, y: Float = 0f, override val inventory: Inventory) : LivingEntity(world, "Player", x, y, 24f, 57f, 50, loadTexture("entity/player.bmp")), AttackListener, DamageListener, InventoryHolder, ItemSelectable by InventoryItemSelector(inventory), Movable, InputListener {
	private val game = EndlessDead.instance;
	override val isUpdatableWhileFrozen = true;
	private val textureWithGun = loadTexture("entity/player_holding_gun.bmp");
	// 타이머
	private val timers = TimerManager();
	private val healTimer: RepeatingTimer;
	override val damageInvincibilityDuration = 0.2f;
	private var wrLastAttackVictim: WeakReference<LivingEntity>? = null;
	/**
	 * 마지막 공객 대상
	 */
	val lastAttackVictim: LivingEntity?
		get() = wrLastAttackVictim?.get();
	// 컴포넌트
	private val moveComponent = MoveComponent(this, 200f);
	override val speed: Float by moveComponent::speed;  // 디컴파일해서 확인한 결과 수동 get() = moveComponent.speed와 오버헤드는 똑같음
	private val dropComponent = ItemDropComponent(this);
	private val pickupComponent = ItemPickupComponent(this);
	private val speedPotionTimers = IdentitySet<Timer>(16);
	/**
	 * 점수 관리자
	 */
	@JvmField val scoreManager = ScoreManager();
	/**
	 * 통계 관리자
	 */
	@JvmField val statistics = StatisticsManager();

	/**
	 * 플레이어를 생성한다.
	 *
	 * @param world 플레이어가 속한 월드
	 * @param x     처음 X 좌표
	 * @param y     처음 Y 좌표
	 */
	constructor(world: World? = null, x: Float = 0f, y: Float = 0f) : this(world, x, y, LinearInventory(-1));

	init {
		// 타이머

		// 1. 생존 시간 기록 & 생존 시간 보너스
		timers.register(RepeatingTimer(1f) {
			statistics.survivedDuration++;
			scoreManager.addScore(1);
		});

		// 2. 30초마다 자연 회복
		healTimer = RepeatingTimer(30f) {
			heal(3);
		}.also { timers.register(it) };

		team = "friends";

		game.inputListeners.register(this) { player, game -> game is EndlessDead && player === game.player && game.gameManager.isPlaying };
	}

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		moveComponent.move(delta, directionX, directionY);
	}

	// ---- 매 프레임 로직 ----

	override fun update(delta: Float) {
		// 타이머 갱신
		timers.update(delta);

		super.update(delta);

		// 반디 위치에 따라 플레이어 회전
		rotateToCursor();

		// 아이템 가져가기 & 넣기
		if(Input.isKeyJustPressed(Input.SPACE) || Input.isButtonJustPressed(Input.RIGHT_MOUSE))
			interactContainer();

		// 아이템 사용
		selectedItem?.let {
			if(it is Usable && (Input.isButtonJustPressed(Input.LEFT_MOUSE) || (it.allowContinuousUse && Input.isButtonPressed(Input.LEFT_MOUSE))))
				useItem(it);
		};

		// 아이템 파괴
		if(Input.isKeyJustPressed(Input.DELETE))
			selectedItem?.let {
				if(it.destroy())
					level?.projector?.drawSubtitles("${it.name} destroyed");
			};

		// 주변 아이템 줍기
		pickupComponent.pickupNearbyItems();

		// 현재 선택한 아이템 버리기
		if(Input.isKeyJustPressed(Input.BACKTICK))
			selectedItem?.let { dropComponent.dropItem(it) };

		// 이동 (총 쏜 이후에 처리할 것.)
		if(Input.isKeyPressed(Input.SHIFT_LEFT))
			moveComponent.speedModifier = 0.5f;
		else
			moveComponent.speedModifier = 1f;
		val moved = updatePosition(delta);
		if(moved)
			level?.updateOffset();
	}

	// 휠로 아이템 선택
	override fun onScroll(amountX: Float, amountY: Float): Boolean {
		if(amountY > 0f) {
			selectNextItem();
			return true;
		} else if(amountY < 0f) {
			selectPreviousItem();
			return true;
		}
		return false;
	}

	/**
	 * 자판 방향 글쇠 눌림 상태에 따라 위치 변경
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 * @return 조금이라도 이동했는지 여부
	 */
	private inline fun updatePosition(delta: Float): Boolean {  // update()에서만 한 번 쓰이기 때문에 inline이다.
		val originalX = x;
		val originalY = y;

		var dx = 0f;
		var dy = 0f;
		if(Input.isKeyPressed(Input.LEFT) || Input.isKeyPressed(Input.A))
			dx -= 1f;
		if(Input.isKeyPressed(Input.RIGHT) || Input.isKeyPressed(Input.D))
			dx += 1f;
		if(Input.isKeyPressed(Input.UP) || Input.isKeyPressed(Input.W))
			dy += 1f;
		if(Input.isKeyPressed(Input.DOWN) || Input.isKeyPressed(Input.S))
			dy -= 1f;
		move(delta, dx, dy);

		// 월드 경계 안쪽으로 가두기.
		val world = level;
		if(world != null) {
			val finalX = x.coerceIn(0f - 0.1f, world.width + 0.1f);  // 0.1: 렌더링 버그를 막기 위한 미세 오프셋
			val finalY = y.coerceIn(0f - 0.1f, world.height + 0.1f);
			if(x != finalX) x = finalX;
			if(y != finalY) y = finalY;
		}

		return x != originalX || y != originalY;
	}

	/**
	 * 닿아 있는 상자와 상호작용한다.
	 */
	private inline fun interactContainer() {  // update()에서만 한 번 쓰이기 때문에 inline이다.
		val mainPlayer = game.player;
		val projector = level?.projector;
		forEachNearby { container ->
			if(container !is Container || !collidesWith(container)) return@forEachNearby;
			if(container.isEmpty) {
				putItemToContainer(container);
			} else {
				if(container is HardContainer && !container.opened) {
					if(this === mainPlayer) {
						if(container.opening) {
							projector?.drawSubtitles("Waiting for the container to unlock", Color.YELLOW);
							beep2();
						} else {
							projector?.drawSubtitles("Opening the container...");
						}
					}
					container.open(this) { container, _ -> takeItemFromContainer(container) };
				} else {
					takeItemFromContainer(container);
				}
			}
		};
	}

	private inline fun putItemToContainer(container: Container) {
		selectedItem?.let {
			if(this === game.player)
				level?.projector?.drawSubtitles("Put ${it.name} into the container");
			container.putItem(it, true);
		} ?: run {
			if(this === game.player) {
				level?.projector?.drawSubtitles("Can't take any item; container is empty", Color.SALMON);
				beep2();
			}
		};
	}

	private inline fun takeItemFromContainer(container: Container) {
		val isPlayerItem = container.isPlayerItem;
		val item: Item? = container.takeItem(this);
		if(item == null) {
			if(this === game.player) {
				level?.projector?.drawSubtitles("Failed to take the item in the container", Color.SALMON);
				beep2();
			}
		} else {
			selectItem(item);
			if(this === game.player)
				level?.projector?.drawSubtitles("Took ${item.name} from the container");
			if(!isPlayerItem)
				statistics.openedContainerCount++;
		}
	}

	// ---- 콜백(이벤트) 처리 ----

	// 대미지를 받았을 때 자연 회복 타이머를 초기화하고 점수를 감점한다
	override fun onDamage(damage: Int, attacker: Entity?) {
		healTimer.reset();

		scoreManager.subtractScore(5);
		statistics.totalDamage += damage;
	}

	// 죽으면 모든 템 떨구기
	override fun onDeath(killer: Entity?) {
		dropComponent.dropAll();
	}

	// 처치한 좀비 수를 갱신하고 점수를 준다.
	override fun onKill(victim: LivingEntity) {
		if(victim is KillScorable)
			scoreManager.addScore(victim.killScore);

		if(victim is Zombie)
			statistics.killedZombieCount++;
	}

	override fun onAttack(victim: LivingEntity) {
		if(victim !is Bullet && victim !is Landmine && lastAttackVictim !== victim)
			wrLastAttackVictim = WeakReference(victim);

		if(victim is AttackScorable)
			scoreManager.addScore(victim.attackScore);
	}

	// ---- 그 외 유틸들 ----

	/**
	 * 플레이어가 갖고 있는 아이템을 사용한다.
	 *
	 * 들고 있지 않은 아이템이거나 사용 가능한 아이템이 아니라면 실패한다.
	 *
	 * @param item 사용할 아이템
	 * @return 성공 여부
	 */
	fun useItem(item: Item): Boolean {
		if(!inventory.hasItem(item) || item !is Usable) return false;
		val succeeded = item.use(this);
		if(succeeded) {
			if(item is Shootable)
				statistics.fireCount++;
		}
		return succeeded;
	}

	/**
	 * 플레이어의 속도를 임시로 증가한다.
	 *
	 * @param amount   증가율
	 * @param duration 활성 기간
	 */
	fun increaseSpeed(amount: Float, duration: Float) {
		moveComponent.speedAddend += amount;

		lateinit var timer: Timer;  // 바뀔 일 없는 변수
		timer = Timer(duration) {
			moveComponent.speedAddend -= amount;
			speedPotionTimers.remove(timer);
		}.also { speedPotionTimers.add(it) };
		timers.register(timer);
	}

	/**
	 * 속도 증감치와 배수를 초기화한다.
	 */
	fun resetSpeed() {
		moveComponent.reset();

		val iterator = speedPotionTimers.iterator();
		while(iterator.hasNext) {
			val timer = iterator.next();
			timers.unregister(timer);
			iterator.remove();
		}
	}

	// 플레이어를 들고 있는 아이템에 맞게 그린다.
	override fun draw(batch: SpriteBatch) {
		val textureOverride = when(selectedItem) {
			is Gun	-> textureWithGun
			else	-> null
		};
		super.draw(batch, textureOverride, null);
	}

	override fun dispose() {
		super<LivingEntity>.dispose();
		super<InventoryHolder>.dispose();
		textureWithGun.dispose();
	}
}
