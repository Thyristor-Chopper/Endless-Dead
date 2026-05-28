package com.oop.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import com.oop.game.GameManager;
import com.oop.game.GameState;
import com.oop.game.InputHandler;
import com.oop.game.ScoreManager;
import com.oop.game.Timer;
import com.oop.game.TimerManager;
import com.oop.game.entity.Zombie;
import com.oop.game.entity.container.Container;
import com.oop.game.item.Gun;
import com.oop.game.item.Item;
import com.oop.game.item.Usable;
import com.oop.game.world.World;
import com.oop.game.world.ZombieWorld;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  플레이어 예제 — player.png 이미지, 화살표 키로 조종.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  GameObject 를 상속하는 '가장 단순한' 예제다.
 *  자기 프로젝트의 Player 를 만들 때 이 파일을 통째로 복사해서
 *  texture 의 파일명을 자기 이미지로 바꾸거나,
 *  update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 *  핵심 포인트:
 *   ▸ Texture 는 객체가 살아있는 동안 한 번만 만들고 재사용 (생성 비용이 큼).
 *   ▸ 객체가 사라질 때 dispose() 로 GPU 자원 해제 — 기본 GameObject.dispose()를 override.
 *   ▸ batch.draw(texture, x, y, w, h) 한 줄로 이미지를 그린다.
 */
class Player(world: World, x: Float, y: Float) : LivingEntity(world, x, y, Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT, "player.bmp", 50) {
	companion object {
		const val PLAYER_WIDTH = 30f;
		const val PLAYER_HEIGHT = 30f;
	}
	
    private var speed = 200f
	override val defaultInvincibleDuration = 0.2f //플레이어 무적시간 조정으로 난이도 조절
	// 타이머
	private val timerManager = TimerManager();
	private val healTimer: Timer
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
	// 인벤토리
	private val inventory = mutableListOf<Item>();
	var selectedItemIndex: Int? = null
		private set;
	val selectedItem: Item?
		get() = selectedItemIndex?.let { inventory[it] };
	val inventoryItemCount: Int
		get() = inventory.size;
	
	init {
		// https://stackoverflow.com/questions/17644429/libgdx-mouse-just-clicked 참고함
		Gdx.input.setInputProcessor(object : InputProcessor {
			// 휠 감지 - 선택된 아이템 전환
			override fun scrolled(amountX: Float, amountY: Float): Boolean {
				if(GameManager.state != GameState.IN_PLAY) return false;
				
				if(amountY != 0.0f) {
					if(amountY > 0f) selectNextItem();
					else if(amountY < 0f) selectPreviousItem();
					return true;
				}
				
				return false;
			}
			
			// 나머지 (스텁)
			override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean = false;
			
			override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean = false;
			
			override fun keyDown(code: Int): Boolean = false;
			
			override fun keyUp(code: Int): Boolean = false;
			
			override fun keyTyped(char: Char): Boolean = false;
			
			override fun touchCancelled(x: Int, y: Int, pointer: Int, button: Int): Boolean = false;
			
			override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean = false;
			
			override fun mouseMoved(x: Int, y: Int): Boolean = false;
		});
		
		// -- 타이머들 --
		// 1. 생존 시간 기록 & 생존 시간 보너스
		timerManager.registerTimer(Timer(1) {
			survivedDuration++;
			ScoreManager.addScore(1);
		});
		
		// 2. 30초마다 자연 회복
		healTimer = Timer(30) {
			heal(3);
		};
		timerManager.registerTimer(healTimer);
	}
	
	/**
	 * 자판 방향 글쇠 눌림 상태에 따라 위치 변경
	 * update()에서만 한 번 쓰이기 때문에 inline
	 *
	 * @return 조금이라도 이동했는지 여부
	 */
	private inline fun updatePosition(delta: Float): Boolean {
		var moved = false;
		if(InputHandler.isKeyPressed(InputHandler.LEFT) || InputHandler.isKeyPressed(InputHandler.A)) {
			x -= speed * delta;
			moved = true;
		}
		if(InputHandler.isKeyPressed(InputHandler.RIGHT) || InputHandler.isKeyPressed(InputHandler.D)) {
			x += speed * delta;
			moved = true;
        }
		if(InputHandler.isKeyPressed(InputHandler.UP) || InputHandler.isKeyPressed(InputHandler.W)) {
			y += speed * delta;
			moved = true;
        }
		if(InputHandler.isKeyPressed(InputHandler.DOWN) || InputHandler.isKeyPressed(InputHandler.S)) {
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
						removeItemFromInventory(it);
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
			if(it is Usable && (InputHandler.isButtonJustPressed(InputHandler.LEFT_MOUSE) || (it.allowContinuousUse && InputHandler.isButtonPressed(InputHandler.LEFT_MOUSE))))
				useItem(it);
		};
		
		// 이동
		val moved = updatePosition(delta);
		if(moved) world.updateCameraOffset();
		
		// 아이템 가져가기 & 넣기
		if(InputHandler.isKeyJustPressed(InputHandler.SPACE) || InputHandler.isButtonJustPressed(InputHandler.RIGHT_MOUSE))
			interactContainer();
		
        // 월드 경계 안쪽으로 가두기.
        x = x.coerceIn(0f, world.width - width);
        y = y.coerceIn(0f, world.height - height);
		
		// 타이머 갱신
		timerManager.tick(delta);
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
	
	fun speedUp(amount: Float) {
		speed += amount;
	}
	
	// ---- 인벤토리 관련 ----
	
	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item	추가할 아이템
	 */
	fun addItemToInventory(item: Item, select: Boolean = false) {
		inventory.add(item);
		if(selectedItemIndex == null)
			selectedItemIndex = 0;
		else if(select)
			selectedItemIndex = inventory.size - 1;
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param index	아이템 위치
	 */
	fun removeItemFromInventory(index: Int) {
		val currentIndex: Int? = selectedItemIndex;
		inventory[index].holder = null;
		inventory.removeAt(index);
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == currentIndex) {
			if(currentIndex == 0) selectedItemIndex = 1;
			else selectedItemIndex = (selectedItemIndex ?: 1) - 1;
		}
	}
	
	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param 	item	제거할 아이템
	 * @return 	성공 여부
	 */
	fun removeItemFromInventory(item: Item): Boolean {
		var found = false;
		if(inventory.size > 0)
			for(i in 0 until inventory.size)
				if(inventory[i] === item) {
					found = true;
					inventory[i].holder = null;
					inventory.removeAt(i);
					if(i == selectedItemIndex)
						selectPreviousItem();
					break;
				}
		if(inventory.isEmpty())
			selectedItemIndex = null;
		return found;
	}
	
	/**
	 * 인벤토리의 다음 아이템 선택
	 */
	fun selectNextItem() {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index >= inventory.size - 1)
			selectedItemIndex = 0;
		else
			selectedItemIndex = (selectedItemIndex ?: 0) + 1;
	}
	
	/**
	 * 인벤토리의 이전 아이템 선택
	 */
	fun selectPreviousItem() {
		val index: Int? = selectedItemIndex;
		if(inventory.isEmpty())
			selectedItemIndex = null;
		else if(index == null)
			selectedItemIndex = 0;
		else if(index <= 0)
			selectedItemIndex = inventory.size - 1;
		else
			selectedItemIndex = (selectedItemIndex ?: 1) - 1;
	}
	
	/**
	 * 지정한 아이템을 갖고 있다면 선택한다.
	 *
	 * @return 성공 여부
	 */
	fun selectItem(item: Item): Boolean {
		val index = inventory.indexOfFirst({ it === item });
		if(index == -1) return false;
		selectedItemIndex = index;
		return true;
	}
	
	/**
	 * 지정한 인덱스의 아이템을 선택한다.
	 */
	fun selectItem(index: Int) {
		if(index < 0 || index >= inventory.size) throw IllegalArgumentException("index out of bounds");
		selectedItemIndex = index;
	}
	
	/**
	 * 지정한 아이템이 있는지 확인
	 */
	fun hasItem(item: Item): Boolean = item in inventory;
	
	/**
	 * 인벤토리의 읽기용 사본을 가져온다.
	 */
	fun getInventory(): List<Item> = inventory.toList();
}
