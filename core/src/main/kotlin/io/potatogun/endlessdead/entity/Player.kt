package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.item.Fireable;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Usable;
import io.potatogun.gdxhelper.Input;
import io.potatogun.gdxhelper.Timer;
import io.potatogun.gdxhelper.TimerManager;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.TextureManager;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  플레이어 — player.bmp 이미지, 화살표 키로 조종.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  Entity를 상속하는 '가장 단순한' 예제다.
 *  자기 프로젝트의 Player 를 만들 때 이 파일을 통째로 복사해서
 *  texture 의 파일명을 자기 이미지로 바꾸거나,
 *  update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 *  핵심 포인트:
 *   ▸ Texture 는 객체가 살아있는 동안 한 번만 만들고 재사용 (생성 비용이 큼).
 *   ▸ 객체가 사라질 때 dispose() 로 GPU 자원 해제 — 기본 Entity#dispose()를 override.
 *   ▸ batch.draw(texture, x, y, w, h) 한 줄로 이미지를 그린다.
 */
class Player(world: World, position: Position) : LivingEntity(world, position, 24f, 57f, TextureManager.loadTexture("entity/player.bmp"), 50), InventoryEntity by InventoryEntityImpl() {
	override val canUpdateWhileFrozen = true;
	private val textureWithGun = TextureManager.loadTexture("entity/player_holding_gun.bmp");
    private var speed = 200f
	override val defaultInvincibleDuration = 0.2f //플레이어 무적시간 조정으로 난이도 조절
	// 타이머
	private val timerManager = TimerManager();
	private val healTimer: Timer;
	// 통계
	var survivedDuration = 0
		private set;
	var openedContainerCount = 0
		private set;
	var killedZombieCount = 0
		private set;
	var fireCount = 0
		private set;
	var totalDamage = 0
		private set;

	init {
		// 타이머

		// 1. 생존 시간 기록 & 생존 시간 보너스
		timerManager.registerTimer(Timer(1f) {
			survivedDuration++;
			ScoreManager.addScore(1);
		});

		// 2. 30초마다 자연 회복
		healTimer = timerManager.registerTimer(Timer(30f) {
			heal(3);
		});
	}

	// ---- 매 프레임 로직 ----

    override fun update(delta: Float) {
		timerManager.tick(delta);

		super.update(delta);

		// 반디 위치에 따라 플레이어 회전
		rotateTo(Position(Input.mouseX.toFloat(), Input.mouseY.toFloat()));

		// 아이템 가져가기 & 넣기
		if(Input.isKeyJustPressed(Input.SPACE) || Input.isButtonJustPressed(Input.RIGHT_MOUSE))
			interactContainer();

		// 아이템 사용
		selectedItem?.let {
			if(it is Usable && (Input.isButtonJustPressed(Input.LEFT_MOUSE) || (it.isContinuousUseAllowed && Input.isButtonPressed(Input.LEFT_MOUSE))))
				useItem(it);
		};

		// 아이템 파괴
		if(Input.isKeyJustPressed(Input.DELETE))
			selectedItem?.let {
				if(it.destroy())
					world.viewer?.drawSubtitles("${it.name} destroyed");
			};

		// 휠로 아이템 선택
		if(Input.isScrolledDown())
			selectNextItem();
		if(Input.isScrolledUp())
			selectPreviousItem();

		// 이동
		val moved = updatePosition(delta);
		if(moved) world.updateCamera();
    }

	/**
	 * 자판 방향 글쇠 눌림 상태에 따라 위치 변경
	 *
	 * update()에서만 한 번 쓰이기 때문에 inline이다.
	 *
	 * @return 조금이라도 이동했는지 여부
	 */
	private inline fun updatePosition(delta: Float): Boolean {
		var moved = false;
		if(Input.isKeyPressed(Input.LEFT) || Input.isKeyPressed(Input.A)) {
			x -= speed * delta;
			moved = true;
		}
		if(Input.isKeyPressed(Input.RIGHT) || Input.isKeyPressed(Input.D)) {
			x += speed * delta;
			moved = true;
        }
		if(Input.isKeyPressed(Input.UP) || Input.isKeyPressed(Input.W)) {
			y += speed * delta;
			moved = true;
        }
		if(Input.isKeyPressed(Input.DOWN) || Input.isKeyPressed(Input.S)) {
			y -= speed * delta;
			moved = true;
		}

        // 월드 경계 안쪽으로 가두기.
		val originalX = x;
		val originalY = y;
        x = x.coerceIn(0f, world.width);
        y = y.coerceIn(0f, world.height);
		moved = moved || x != originalX || y != originalY;

		return moved;
	}

	/**
	 * 닿아 있는 상자와 상호작용한다.
	 *
	 * update()에서만 한 번 쓰이기 때문에 inline이다.
	 */
	private inline fun interactContainer() {
		for(entity in world.getEntities()) {
			if(entity !is Container) continue;
			if(collidesWith(entity))
				if(entity.isEmpty) {
					var putItem = false;
					selectedItem?.let {
						world.viewer?.drawSubtitles("Put ${it.name} into the container");
						entity.putItem(it, true);
						putItem = true;  // 하나씩만
					} ?: world.viewer?.drawSubtitles("Can't take any item; container is empty");
					if(putItem) break;
				} else {
					val isPlayerItem = entity.isPlayerItem;
					val item: Item? = entity.takeItem(this, true);
					if(item == null) {
						world.viewer?.drawSubtitles("Failed to take the item in the container");
					} else {
						world.viewer?.drawSubtitles("Took ${item.name} from the container");
						if(!isPlayerItem)
							openedContainerCount++;
						break;  // 하나씩만
					}
				}
		}
	}

	// ---- 콜백(이벤트) 처리 ----

	/**
	 * 대미지를 받았을 때 자연 회복 타이머를 초기화하고 점수를 감점한다
	 */
	override fun onDamage(damage: Int, attacker: Entity?) {
		healTimer.reset();
		ScoreManager.subtractScore(5);
		totalDamage += damage;
	}

	/**
	 * 처치한 좀비 수를 갱신한다.
	 */
	override fun onKill(victim: Entity) {
		if(victim is Zombie) {
			ScoreManager.addScore(10);
			killedZombieCount++;
		}
	}

	// ---- 그 외 유틸들 ----

	/**
	 * 플레이어가 갖고 있는 아이템을 사용한다.
	 *
	 * 들고 있지 않은 아이템이거나 사용 가능한 아이템이 아니라면 실패한다.
	 *
	 * @param	item	사용할 아이템
	 * @return	성공 여부
	 */
	fun useItem(item: Item): Boolean {
		if(!hasItem(item) || item !is Usable) return false;
		val succeeded = item.use();
		if(succeeded)
			if(item is Fireable)
				fireCount++;
		return succeeded;
	}

	/**
	 * 플레이어의 속도를 임시로 증가한다.
	 *
	 * @param amount	증가율
	 * @param duration	활성 기간
	 */
	fun speedUp(amount: Float, duration: Float) {
		speed += amount;
		Utils.setTimeout(duration) {
			speed -= amount;
		};
	}

	/**
	 * 플레이어를 들고 있는 아이템에 맞게 그린다.
	 */
	override fun draw(batch: SpriteBatch) {
		val texture = if(selectedItem is Gun) textureWithGun else this.texture;
		super.draw(batch, texture);
	}

	/**
	 * 자원 및 타이머 정리
	 */
	override fun dispose() {
		super.dispose();
		textureWithGun.dispose();
		timerManager.clearTimers();
	}
}
