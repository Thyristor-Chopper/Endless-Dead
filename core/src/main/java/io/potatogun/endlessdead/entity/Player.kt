package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.Statistics;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.entity.component.MeleeAttackComponent;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.endlessdead.entity.component.ItemDropComponent;
import io.potatogun.endlessdead.entity.component.ItemPickupComponent;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.entity.listener.AttackListener;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.inventory.LinearInventory;
import io.potatogun.endlessdead.inventory.ObservableInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.item.Usable;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.Input;
import io.potatogun.gdxhelper.util.TextureUtils;
import io.potatogun.gdxhelper.world.World;

import java.lang.ref.WeakReference;

/**
 * 플레이어 — 화살표로 조종
 */
class Player private constructor(world: World, x: Float, y: Float, override val inventory: ObservableInventory) : LivingEntity(world, "Player", x, y, 24f, 57f, 50, TextureUtils.loadTexture("entity/player.bmp")), AttackListener, DamageListener, InventoryHolder, ItemSelectable by InventoryItemSelector(inventory), MeleeAttackable, Movable {
	override val isUpdatableWhileFrozen = true;
	private val textureWithGun = TextureUtils.loadTexture("entity/player_holding_gun.bmp");
	// 타이머
	private val timerManager = TimerManager();
	private val healTimer: RepeatingTimer;
	override val damageInvincibilityDuration = 0.01f;
	private var _latestAttackVictim: WeakReference<LivingEntity>? = null;
	val latestAttackVictim: LivingEntity?
		get() = _latestAttackVictim?.get();
	// 컴포넌트
	private val meleeAttackComponent = MeleeAttackComponent(this, 1, 0.4f);
	override val attackDamage: Int by meleeAttackComponent::attackDamage;
	override val attackInterval: Float by meleeAttackComponent::attackInterval;
	private val moveComponent = MoveComponent(this, 200f);
	override val speed: Float by moveComponent::speed;  // 디컴파일해서 확인한 결과 수동 get() = moveComponent.speed와 오버헤드는 똑같음
	private val dropComponent = ItemDropComponent(this);
	private val pickupComponent = ItemPickupComponent(this);

	/**
	 * 플레이어를 생성한다.
	 *
	 * @param world 플레이어가 속한 월드
	 * @param x     처음 X 좌표
	 * @param y     처음 Y 좌표
	 */
	constructor(world: World, x: Float, y: Float) : this(world, x, y, LinearInventory(-1));

	init {
		// 타이머

		// 1. 생존 시간 기록 & 생존 시간 보너스
		timerManager.register(RepeatingTimer(1f) {
			Statistics.survivedDuration++;
			ScoreManager.addScore(1);
		});

		// 2. 30초마다 자연 회복
		healTimer = RepeatingTimer(30f) {
			heal(3);
		}.also { timerManager.register(it) };

		team = "friends";
	}

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		moveComponent.move(delta, directionX, directionY);
	}

	override fun damageTarget(target: LivingEntity): Boolean = meleeAttackComponent.damageTarget(target);

	override fun meleeAttackNearby() {
		meleeAttackComponent.meleeAttackNearby();
	}

	// ---- 매 프레임 로직 ----

	override fun update(delta: Float) {
		// 타이머 갱신
		timerManager.tick(delta);

		super.update(delta);

		// 컴포넌트 갱신
		meleeAttackComponent.update(delta);

		// 반디 위치에 따라 플레이어 회전
		rotateToCursor();

		// 아이템 가져가기 & 넣기
		if(Input.isKeyJustPressed(Input.SPACE) || Input.isButtonJustPressed(Input.RIGHT_MOUSE))
			interactContainer();

		// 아이템 사용 및 손공격
		selectedItem?.let {
			if(it is Usable && (Input.isButtonJustPressed(Input.LEFT_MOUSE) || (it.isContinuousUseAllowed && Input.isButtonPressed(Input.LEFT_MOUSE))))
				useItem(it);
		} ?: run {
			if(Input.isButtonJustPressed(Input.LEFT_MOUSE))
				meleeAttackComponent.meleeAttackNearby();
		};

		// 아이템 파괴
		if(Input.isKeyJustPressed(Input.DELETE))
			selectedItem?.let {
				if(it.destroy())
					world.projector?.drawSubtitles("${it.name} destroyed");
			};

		// 휠로 아이템 선택
		if(Input.isScrolledDown())
			selectNextItem();
		if(Input.isScrolledUp())
			selectPreviousItem();

		// 주변 아이템 줍기
		pickupComponent.pickupNearbyItems();

		// 현재 선택한 아이템 버리기
		if(Input.isKeyJustPressed(Input.BACKTICK))
			selectedItem?.let { dropComponent.dropItem(it) };

		// 이동 (총 쏜 이후에 처리할 것.)
		val moved = updatePosition(delta);
		if(moved) world.updateOffset();
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

		if(Input.isKeyPressed(Input.LEFT) || Input.isKeyPressed(Input.A))
			move(delta, -1f, 0f);
		if(Input.isKeyPressed(Input.RIGHT) || Input.isKeyPressed(Input.D))
			move(delta, 1f, 0f);
		if(Input.isKeyPressed(Input.UP) || Input.isKeyPressed(Input.W))
			move(delta, 0f, 1f);
		if(Input.isKeyPressed(Input.DOWN) || Input.isKeyPressed(Input.S))
			move(delta, 0f, -1f);

		// 월드 경계 안쪽으로 가두기.
		x = x.coerceIn(0f, world.width);
		y = y.coerceIn(0f, world.height);

		return x != originalX || y != originalY;
	}

	/**
	 * 닿아 있는 상자와 상호작용한다.
	 */
	private inline fun interactContainer() {  // update()에서만 한 번 쓰이기 때문에 inline이다.
		val projector = world.projector;
		forEachNearby { entity ->
			if(entity !is Container || !collidesWith(entity)) return@forEachNearby;
			if(entity.inventory.isEmpty) {
				selectedItem?.let {
					projector?.drawSubtitles("Put ${it.name} into the container");
					entity.putItem(it, true);
				} ?: projector?.drawSubtitles("Can't take any item; container is empty");
			} else {
				val isPlayerItem = entity.isPlayerItem;
				val item: Item? = entity.takeItem(this);
				if(item == null) {
					projector?.drawSubtitles("Failed to take the item in the container");
				} else {
					selectItem(item);
					projector?.drawSubtitles("Took ${item.name} from the container");
					if(!isPlayerItem)
						Statistics.openedContainerCount++;
				}
			}
		};
	}

	// ---- 콜백(이벤트) 처리 ----

	// 대미지를 받았을 때 자연 회복 타이머를 초기화하고 점수를 감점한다
	override fun onDamage(damage: Int, attacker: Entity?) {
		healTimer.reset();
		ScoreManager.subtractScore(5);
		Statistics.totalDamage += damage;
	}

	// 처치한 좀비 수를 갱신한다.
	override fun onKill(victim: LivingEntity) {
		if(victim is Zombie) {
			ScoreManager.addScore(10);
			Statistics.killedZombieCount++;
		}
	}

	override fun onAttack(victim: LivingEntity) {
		_latestAttackVictim = WeakReference(victim);
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
		if(succeeded)
			if(item is Shootable)
				Statistics.fireCount++;
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
		timerManager.register(Timer(duration) { moveComponent.speedAddend -= amount });
	}

	// 플레이어를 들고 있는 아이템에 맞게 그린다.
	override fun draw(batch: SpriteBatch) {
		val textureOverride = when(selectedItem) {
			is Gun	-> textureWithGun
			else	-> null
		};
		super.draw(batch, textureOverride, null);
	}

	// 자원 및 타이머 정리
	override fun dispose() {
		super.dispose();
		textureWithGun.dispose();
	}
}
