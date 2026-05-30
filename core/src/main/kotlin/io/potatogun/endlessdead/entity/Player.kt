package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Input;
import io.potatogun.endlessdead.ScoreManager;
import io.potatogun.endlessdead.Timer;
import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Usable;
import io.potatogun.endlessdead.world.World;
import io.potatogun.endlessdead.world.ZombieWorld;

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
class Player(world: World, x: Float, y: Float) : LivingEntity(world, x, y, 24f, 57f, "player.bmp", 50), InventoryEntity by InventoryEntityImpl() {
	override val canUpdateWhileFrozen = true;
	private val textureWithGun = Utils.loadTexture("player_holding_gun.bmp");
    private var speed = 200f
	override val defaultInvincibleDuration = 0.2f //플레이어 무적시간 조정으로 난이도 조절
	// 타이머
	private val healTimer: Timer;
	private val timers = mutableListOf<Timer>();
	// 통계
	var survivedDuration = 0
		private set;
	var openedContainerCount = 0
		private set;
	var killedZombieCount = 0
		private set;
	var firedBullets = 0
		private set;
	var totalDamage = 0
		private set;
	
	init {
		// -- 타이머들 --
		// 1. 생존 시간 기록 & 생존 시간 보너스
		timers.add(Timer(1f) {
			survivedDuration++;
			ScoreManager.addScore(1);
		}.register());
		
		// 2. 30초마다 자연 회복
		healTimer = Timer(30f) {
			heal(3);
		}.register();
		timers.add(healTimer);
	}
	
	private inline fun rotatePlayer(x: Int, y: Int) {
		// 샷건 내 360도 구현 참고함
		rotation = toDegrees(atan2((world.game.screenHeight - y) - (this.y - world.offsetY), x - (this.x - world.offsetX)).toDouble()).toFloat() - 90f;
	}
	
	/**
	 * 자판 방향 글쇠 눌림 상태에 따라 위치 변경
	 * update()에서만 한 번 쓰이기 때문에 inline
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
		return moved;
	}
	
	/**
	 * 플레이어가 갖고 있는 아이템을 사용한다.
	 *
	 * 들고 있지 않은 아이템이거나 사용 가능한 아이템이 아니라면 실패한다.
	 *
	 * @param	item	사용할 아이템
	 * @return	성공 여부
	 */
	fun useItem(item: Item): Boolean {
		if(!hasItem(item) || !(item is Usable)) return false;
		
		val succeeded = item.use();
		if(succeeded)
			if(item is Gun)  // 확장성을 고려하려 && 안 쓰고 중첩 if문 사용함
				firedBullets++;
		return succeeded;
	}
	
	/**
	 * 닿아 있는 상자와 상호작용한다.
	 * update()에서만 한 번 쓰이기 때문에 inline
	 */
	private inline fun interactContainer() {
		for(entity in world.getEntities()) {
			if(!(entity is Container)) continue;
			if(collidesWith(entity))
				if(entity.isEmpty) {
					var putItem = false;
					selectedItem?.let {
						world.drawSubtitles("Put ${it.name} into the container");
						entity.putItem(it, true);
						putItem = true;  // 하나씩만
					} ?: world.drawSubtitles("Can't take any item; container is empty");
					if(putItem) break;
				} else {
					val isPlayerItem = entity.isPlayerItem;
					val item: Item? = entity.takeItem(this, true);
					if(item == null) {
						world.drawSubtitles("Failed to take the item in the container");
					} else {
						world.drawSubtitles("Took ${item.name} from the container");
						if(!isPlayerItem)
							openedContainerCount++;
						break;  // 하나씩만
					}
				}
		}
	}
	
    override fun update(delta: Float) {
		super.update(delta);
		
		// 아이템 사용
		selectedItem?.let {
			if(it is Usable && (Input.isButtonJustPressed(Input.LEFT_MOUSE) || (it.allowContinuousUse && Input.isButtonPressed(Input.LEFT_MOUSE))))
				useItem(it);
		};
		
		// 플레이어 회전
		rotatePlayer(Gdx.input.getX(), Gdx.input.getY());
		
		// 이동
		val moved = updatePosition(delta);
		if(moved) world.updateCameraOffset();
		
		// 아이템 가져가기 & 넣기
		if(Input.isKeyJustPressed(Input.SPACE) || Input.isButtonJustPressed(Input.RIGHT_MOUSE))
			interactContainer();
		
		// 아이템 파괴
		if(Input.isKeyJustPressed(Input.DELETE))
			selectedItem?.let {
				if(it.destroy())
					world.drawSubtitles("${it.name} destroyed");
			};
		
		// 휠로 아이템 선택
		if(Input.isScrolledDown())
			selectNextItem();
		if(Input.isScrolledUp())
			selectPreviousItem();
		
        // 월드 경계 안쪽으로 가두기.
        x = x.coerceIn(0f, world.width);
        y = y.coerceIn(0f, world.height);
    }
	
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
	override fun onKill(victim: LivingEntity) {
		if(victim is Zombie) {
			ScoreManager.addScore(10);
			killedZombieCount++;
		}
	}
	
	fun speedUp(amount: Float, duration: Float) {
		speed += amount;
		Utils.setTimeout(duration) {
			speed -= amount;
		};
	}
	
	override fun draw(batch: SpriteBatch) {
		val texture = if(selectedItem is Gun) textureWithGun else this.texture;
		super.draw(batch, texture);
	}
	
	override fun dispose() {
		super.dispose();
		textureWithGun.dispose();
		for(timer in timers)
			timer.unregister();
	}
}
