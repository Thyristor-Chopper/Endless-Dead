package com.oop.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import com.oop.game.Updatable;
import com.oop.game.WorldObject;
import com.oop.game.ZombieGame;
import com.oop.game.entity.Bullet;
import com.oop.game.entity.Entity;
import com.oop.game.entity.InventoryEntity;
import com.oop.game.entity.LivingEntity;
import com.oop.game.entity.Player;
import com.oop.game.entity.container.Container;
import com.oop.game.item.Item;
import com.oop.game.screen.Screen;

/**
 * 게임 한 장면 = '월드 하나' 의 추상 기본 클래스.
 *
 * ────────────────────────────────────────────────────────────
 *  왜 이런 게 필요한가?
 * ────────────────────────────────────────────────────────────
 *  게임을 만들 때 학생이 다루는 핵심 개념은 '하나의 월드'다:
 *    - 그 안에 어떤 객체들이 있는가
 *    - 객체들이 매 프레임 어떻게 움직이고 상호작용하는가
 *    - 그것을 어떻게 그릴 것인가
 *  GameWorld 는 이 '월드' 를 표현하는 한 클래스에 모든 것을 담는다.
 *
 *  학생은 이 클래스를 상속해 자기 게임의 월드를 만든다 (ExampleWorld 참고).
 *
 * ────────────────────────────────────────────────────────────
 *  두 가지 크기의 차이
 * ────────────────────────────────────────────────────────────
 *  screenWidth/Height  — 카메라가 한 번에 보여주는 영역 (창 크기와 같다)
 *  width/height        — 게임 월드 전체 크기 (화면보다 클 수 있다)
 *  이 둘이 같으면 화면 고정, 월드가 더 크면 카메라(offset) 로 스크롤 가능.
 *
 * ────────────────────────────────────────────────────────────
 *  매 프레임의 표준 흐름 (render 안에서)
 * ────────────────────────────────────────────────────────────
 *    ① 화면 clear
 *    ② update(delta) — 각 객체 갱신, 상호작용, 정리 (서브클래스 override 가능)
 *    ③ batch.begin
 *    ④ drawBackground(batch) — 서브클래스가 그리는 배경 (필수 구현)
 *    ⑤ 모든 게임 객체를 carmera offset 적용해 draw
 *    ⑥ batch.end
 *
 *  학생이 보통 override 하는 것:
 *   ▸ drawBackground(batch) — 자기 배경 그리기 (필수, abstract)
 *   ▸ update(delta)         — 자기 게임 로직 (대부분 override 함)
 *   ▸ render(delta)         — 객체 위에 텍스트/HUD 추가 그리기 (선택)
 *
 * @param screenWidth  화면(카메라)이 보여주는 영역 너비 (픽셀)
 * @param screenHeight 화면(카메라)이 보여주는 영역 높이
 * @param width        월드 전체 너비 (기본값: 화면과 동일 = 스크롤 없음)
 * @param height       월드 전체 높이
 */
abstract class World(game: ZombieGame, val width: Float = game.screenWidth.toFloat(), val height: Float = game.screenHeight.toFloat()) : Screen(game), Updatable {
	abstract val player: Player;
    // 카메라 오프셋 — 월드의 어느 지점이 화면 좌하단에 오는지.
    //   이 두 값만 바꾸면 카메라가 움직이는 효과가 난다.
    var offsetX: Float = width / 2f - game.screenWidth / 2f;
    var offsetY: Float = height / 2f - game.screenHeight / 2f;
    // 등록된 객체들만 update/draw 된다.
    // private 으로 감춘 이유: 외부가 직접 add/remove 하면
    //   '순회 중 삭제' 같은 버그가 나기 쉽다. add(), remove() 라는 공식 창구만 허용.
    //   (5주차에서 배운 캡슐화의 실제 사례)
    private val entities = mutableListOf<Entity>();
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;

    // ────────────────────────────────────────────────────────
    //  객체 관리
    // ────────────────────────────────────────────────────────

    /**
	 * 객체를 월드에 등록 — 이후부터 자동으로 update/draw 된다.
	 */
    fun addEntity(entity: Entity) {
        entities.add(entity);
    }

    /**
	 * 특정 객체를 수동 제거. 보통은 isAlive()=false 후 removeDead() 로 정리.
	 */
    fun removeEntity(entity: Entity) {
        entities.remove(entity);
		entity.dispose();
    }

    /**
     * 현재 등록된 객체 목록의 '읽기용 복사본'.
     *
     * toList() 로 복사해서 주는 이유:
     *   외부가 받은 리스트에 add/remove 하면 내부 상태가 망가진다.
     *   복사본을 줘서 '훔쳐보기만 하고 건드리진 못하게' 한다.
     */
    fun getEntities(): List<Entity> = entities.toList();
	
	/**
	 * 크기 조절 시 호출된다.
	 */
	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		updateCameraOffset();
	}
	
	/**
	 * 월드 내 모든 아이템과 개체를 순회한다.
	 *
	 * @param callback	실행할 서브루틴
	 */
	fun forEachObjects(callback: (WorldObject) -> Unit) {
		for(entity in entities.toList()) {
			callback(entity);
			if(entity is InventoryEntity)
				for(item in entity.getInventory())
					callback(item);
			if(entity is Container)
				entity.containedItem?.let { callback(it) };
		}
	}
	
	/**
	 * 월드 내 모든 아이템을 순회한다.
	 *
	 * @param callback	실행할 서브루틴
	 */
	fun forEachItems(callback: (WorldObject) -> Unit) {
		for(entity in entities.toList()) {
			if(entity is InventoryEntity)
				for(item in entity.getInventory())
					callback(item);
			if(entity is Container)
				entity.containedItem?.let { callback(it) };
		}
	}

    /**
     * 등록된 모든 객체에게 'update(delta) 한 프레임 진행' 을 시킨다.
     *
     * 객체 간 상호작용("누가 누구와 부딪혔는가") 은 여기서 결정하지 않는다.
     * 그건 update() 안에서 이 함수를 호출한 뒤 직접 처리할 일이다.
     *
     * TODO (9주차 이후): 고차함수 forEach 로
     *   gameObjects.forEach { it.update(delta) } 처럼 줄일 수 있다.
     */
    private fun updateAllObjects(delta: Float) {
		forEachObjects {
			if(it is Updatable) {
				if(!(it is Entity) || (it is Entity && (!(this is Freezable) || !this.isFrozen || it.canUpdateWhileFrozen)))
					it.update(delta);
				if(it is Entity)
					it.forceUpdate(delta);
			}
		};
    }

    /**
     * isAlive() 가 false 인 객체들을 한꺼번에 제거한다.
     *
     * 보통 update() 끝에서 호출 — 상호작용 결과 죽음을 표시한 객체를 정리.
     *
     * 순회 도중 삭제 시 인덱스 꼬임을 막으려고 '먼저 모아 두고 → 한꺼번에 삭제' 패턴.
     *
     * TODO (9주차 이후): 컬렉션 함수 removeAll 로
     *   gameObjects.removeAll { !it.isAlive() } 한 줄로 대체 가능.
     */
    private fun removeDead() {
		val toRemove = mutableListOf<Entity>();
        for(entity in entities)
            if(entity is LivingEntity && !entity.isAlive)
                toRemove.add(entity);
        for(entity in toRemove) {
            entities.remove(entity);
			entity.dispose();
		}
    }

    override fun update(delta: Float) {
		updateAllObjects(delta);
		removeDead();
	}
	
    // ────────────────────────────────────────────────────────
    //  매 프레임 그리기
    // ────────────────────────────────────────────────────────

	override fun drawElements(delta: Float) {
        drawBackgroundOverlay();
        drawEntities();
		// 자막이 있으면 표시
		if(subtitlesTimer > 0f) subtitlesMessage?.let {
			drawText(
				text = it,
				x = 0f,
				y = 20f,
				color = subtitlesColor,
				scale = 1.0f,
				width = game.screenWidth.toFloat(),
				align = Align.center,
				skipBatch = true
			);
			subtitlesTimer -= delta;
		};
	}
	
	/**
	 * 월드 중심 등 오버레이를 그리는 자리
	 */
	protected open fun drawBackgroundOverlay() {}

    /**
     * 등록된 모든 객체를 그린다 — 카메라 오프셋을 반영해서.
     *
     * 핵심 트릭: 객체의 월드 좌표에서 offset 을 잠깐 빼서 '화면 좌표' 처럼 만든 뒤
     *           draw() 시키고, 끝나면 원래 월드 좌표로 되돌린다.
     *
     * 이렇게 해야 서브클래스의 draw() 는 '자기 위치에 그냥 그려라' 만 구현하면 되고,
     *   카메라가 움직이든 말든 신경 쓸 필요가 없다.
     */
    private inline fun drawEntities() {
        for(entity in entities) {
            val originalX = entity.x;
            val originalY = entity.y;
            entity.x -= offsetX;
            entity.y -= offsetY;
            entity.draw(batch);
            entity.x = originalX;
            entity.y = originalY;
        }
    }
	
	/**
	 * 플레이어 위치에 따라 카메라 위치 변경
	 */
	inline fun updateCameraOffset() {
		offsetX = player.x - game.screenWidth / 2f;
		offsetY = player.y - game.screenHeight / 2f;
	}

    // ────────────────────────────────────────────────────────
    //  텍스트 헬퍼
    // ────────────────────────────────────────────────────────

    /**
     * 월드 좌표에 텍스트 그리기.
     *
     * 월드의 특정 지점에 고정되므로, 카메라를 움직이면 텍스트도 따라 움직인다.
     *   → 지도 표지판, NPC 머리 위 말풍선, 특정 지역 이름 등에 적합.
     *
     * 구현 원리: 월드 좌표에서 카메라 offset 만큼 빼서 화면 좌표로 바꾼 뒤
     *           drawTextOnScreen 호출.
     */
    fun drawTextInWorld(
        text: String,
        x: Float,
        y: Float,
        color: Color = Color.WHITE,
        scale: Float = 1f,
		width: Float? = null,  // 오른쪽이나 가운데 정렬 시 필요
		align: Int? = null,  // 글자 정렬
		fixedWidthChars: String = "",  // null이 아닌 이유는 실제로 빈 문자열이면 고정폭이 없다는 뜻
		skipBatch: Boolean = false
    ) {
        val screenX = x - offsetX;
        val screenY = y - offsetY;
        drawText(text, screenX, screenY, color, scale, width, align, fixedWidthChars, skipBatch);
    }
	
	/**
	 * 화면 하단에 자막을 표시한다.
	 *
	 * @param message	표시할 내용
	 * @param duration	표시 시간(초)
	 * @param color		글자 색
	 */
	fun drawSubtitles(message: String, duration: Int = 3, color: Color = Color.WHITE) {
		subtitlesTimer = duration.toFloat();
		subtitlesMessage = message;
		subtitlesColor = color;
	}

    override fun dispose() {
        super.dispose();
        for(entity in entities)
            entity.dispose();
		entities.clear();
    }
}
