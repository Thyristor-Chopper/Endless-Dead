package com.oop.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import com.oop.game.GameState;
import com.oop.game.InputHandler
import com.oop.game.Position;
import com.oop.game.ScoreManager;
import com.oop.game.Timer;
import com.oop.game.TimerExecutor;
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
class Player(world: World, x: Float, y: Float) : LivingEntity(world, x, y, Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT, "player.bmp", 50), InventoryEntity, TimerExecutor {
	companion object {
		// 상수
		val PLAYER_WIDTH = 30f;
		val PLAYER_HEIGHT = 30f;
	}
	
	override val inventory = mutableListOf<Item>();
	override var selectedItemIndex: Int? = null;
    private val speed = 200f;
	// 타이머
	override var unitTimer = TimerExecutor.MAX_UNIT_TIMER
		set(value) {
			if(value < 0) field = 0;
			else if(value > TimerExecutor.MAX_UNIT_TIMER) field = TimerExecutor.MAX_UNIT_TIMER;
			else field = value;
		};
	override val timers = mutableListOf<Timer>();
	private val healTimer: Timer;
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
		// https://stackoverflow.com/questions/17644429/libgdx-mouse-just-clicked 참고함
		Gdx.input.setInputProcessor(object : InputProcessor {
			// 휠 감지 - 선택된 아이템 전환
			override fun scrolled(amountX: Float, amountY: Float): Boolean {
				if(world.state != GameState.IN_PLAY) return false;
				
				if(amountY != 0.0f) {
					if(amountY > 0.0f) selectNextItem();
					else if(amountY < 0.0f) selectPreviousItem();
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
		
		// 타이머들
		
		// 생존 시간 기록 & 생존 시간 보너스
		registerTimer(Timer(1) {
			survivedDuration++;
			ScoreManager.addScore(1);
		});
		
		// 자연 회복
		healTimer = Timer(30) {
			heal(3);
		};
		registerTimer(healTimer);
	}
	
    override fun update(delta: Float) {
        super<LivingEntity>.update(delta);
		
		val holding: Item? = selectedItem;
		
		// 이동
        if(InputHandler.isKeyPressed(InputHandler.LEFT) || InputHandler.isKeyPressed(InputHandler.A)) {
			x -= speed * delta;
			world.offsetX = x - world.screenWidth / 2.0f + width / 2.0f;
		}
        if(InputHandler.isKeyPressed(InputHandler.RIGHT) || InputHandler.isKeyPressed(InputHandler.D)) {
			x += speed * delta;
			world.offsetX = x - world.screenWidth / 2.0f + width / 2.0f;
		}
        if(InputHandler.isKeyPressed(InputHandler.UP) || InputHandler.isKeyPressed(InputHandler.W)) {
			y += speed * delta;
			world.offsetY = y - world.screenHeight / 2.0f + height / 2.0f;
		}
        if(InputHandler.isKeyPressed(InputHandler.DOWN) || InputHandler.isKeyPressed(InputHandler.S)) {
			y -= speed * delta;
			world.offsetY = y - world.screenHeight / 2.0f + height / 2.0f;
		}
		
		// 아이템 사용
		if(holding != null && holding is Usable && (InputHandler.isButtonJustPressed(InputHandler.LEFT_MOUSE) || (holding.allowContinuousUse && InputHandler.isButtonPressed(InputHandler.LEFT_MOUSE)))) {
			val succeeded = holding.use();
			if(succeeded && holding is Gun)
				firedBullets++;
		}
		
		// 아이템 가져가기 & 넣기
		if(InputHandler.isKeyJustPressed(InputHandler.SPACE) || InputHandler.isButtonJustPressed(InputHandler.RIGHT_MOUSE))
			for(entity in world.getEntities()) {
				if(!(entity is Container)) continue;
				if(collidesWith(entity)) {
					if(entity.isEmpty) {
						val currentHolding: Item? = selectedItem;
						if(currentHolding != null) {
							world.drawSubtitles("Put ${currentHolding.name} into the container");
							entity.putItem(currentHolding, true);
							removeItemFromInventory(currentHolding);
							break;  // 하나씩만
						} else {
							world.drawSubtitles("Can't take any item; container is empty");
						}
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
		
        // 월드 경계 안쪽으로 가두기.
        x = x.coerceIn(0f, world.width - width);
        y = y.coerceIn(0f, world.height - height);
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
		if(victim is Zombie)
			killedZombieCount++;
	}
}
