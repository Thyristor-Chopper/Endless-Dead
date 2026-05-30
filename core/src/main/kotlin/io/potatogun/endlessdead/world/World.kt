package io.potatogun.endlessdead.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.Entity;
import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Screen;

/**
 * 게임 내 월드 = '월드 하나' 의 추상 기본 클래스.
 * Screen을 확장하여 '월드'의 개념에 맞게 플레이어나 적 등의 개체 등을 추가한다.
 *
 * ────────────────────────────────────────────────────────────
 *  왜 이런 게 필요한가?
 * ────────────────────────────────────────────────────────────
 *  게임을 만들 때 다루는 핵심 개념은 '하나의 월드'다:
 *    - 그 안에 어떤 객체들이 있는가
 *    - 객체들이 매 프레임 어떻게 움직이고 상호작용하는가
 *    - 그것을 어떻게 그릴 것인가
 *  World는 이 '월드'를 표현하는 한 클래스에 모든 것을 담는다.
 *
 *  이 클래스를 상속해 자기 게임의 월드를 만든다 (ZombieWorld 참고).
 *
 * @param game		월드가 속한 게임
 * @param width		월드 전체 너비
 * @param height	월드 전체 높이
 */
abstract class World(game: EndlessDead, val width: Float = game.screenWidth.toFloat(), val height: Float = game.screenHeight.toFloat()) : Screen(game) {
	/**
	 * 이 월드의 플레이어
	 */
	abstract val player: Player;
    /**
	 * 카메라 오프셋 — 월드의 어느 지점이 화면 좌하단에 오는지.
     *   이 두 값만 바꾸면 카메라가 움직이는 효과가 난다.
	 */
    var offsetX: Float = width / 2f - game.screenWidth / 2f;
    var offsetY: Float = height / 2f - game.screenHeight / 2f;
    // 등록된 객체들만 update/draw 된다.
    // private 으로 감춘 이유: 외부가 직접 add/remove 하면
    //   '순회 중 삭제' 같은 버그가 나기 쉽다. addEntity(), removeEntity()라는 공식 창구만 허용.
    //   (5주차에서 배운 캡슐화의 실제 사례)
    private val entities = mutableListOf<Entity>();
	// 자막 타이머 관련 필드들
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;

    // ────────────────────────────────────────────────────────
    //  개체 관리
    // ────────────────────────────────────────────────────────

    /**
	 * 객체를 월드에 등록 — 이후부터 자동으로 update/draw 된다.
	 *
	 * @param entity 추가할 개체
	 */
    fun addEntity(entity: Entity) {
        entities.add(entity);
    }

    /**
	 * 특정 객체를 수동 제거. 보통은 isAlive()=false 후 removeDead() 로 정리.
	 *
	 * @param entity 제거할 개체
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
	 *
	 * @return 읽기 전용 개체 목록
     */
    fun getEntities(): List<Entity> = entities.toList();

    /**
     * 등록된 모든 객체에게 'update(delta) 한 프레임 진행' 을 시킨다.
	 *
	 * update 내에서만 한 번 쓰이기 때문에 inline이다.
     */
    private inline fun updateEntities(delta: Float) {
		for(entity in entities.shuffled()) {
			if(!(this is Freezable) || !this.isFrozen || entity.canUpdateWhileFrozen)
				entity.update(delta);
			entity.forceUpdate(delta);
		}
    }

    /**
     * isAlive가 false인 객체들을 한꺼번에 제거한다.
     *
     * 보통 update() 끝에서 호출 — 상호작용 결과 죽음을 표시한 객체를 정리.
     *
     * 순회 도중 삭제 시 인덱스 꼬임을 막으려고 '먼저 모아 두고 → 한꺼번에 삭제' 패턴.
	 *
	 * update 내에서만 한 번 쓰이기 때문에 inline이다.
     */
    private inline fun removeDead() {
		val toRemove = mutableListOf<Entity>();
        for(entity in entities)
            if(entity is LivingEntity && !entity.isAlive)
                toRemove.add(entity);
        for(entity in toRemove) {
            entities.remove(entity);
			entity.dispose();
		}
    }

    // ────────────────────────────────────────────────────────
    //  콜백 함수
    // ────────────────────────────────────────────────────────

	/**
	 * 크기 조절 시 호출된다.
	 */
	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		updateCameraOffset();
	}

    // ────────────────────────────────────────────────────────
    //  매 프레임 로직
    // ────────────────────────────────────────────────────────

	/**
     * 매 프레임 게임 로직 — 서브클래스가 override 해서 자기 게임 로직을 넣는 곳.
     *
     * 기본 구현은 가장 단순한 '갱신 → 정리' 시나리오를 보여준다:
     *   ① updateAllObjects(delta) — 각 객체가 자기 위치 갱신
     *   ② removeDead()            — isAlive=false인 객체 제거
     *
     * 객체 간 상호작용(충돌·점수·생사 결정)이 있는 게임이면 override해서
     * 위 두 호출 사이에 그 로직을 끼워 넣는다 (ZombieWorld 참고).
     */
    override fun update(delta: Float) {
		updateEntities(delta);
		removeDead();
		if(subtitlesTimer > 0f)
			subtitlesTimer -= delta;
	}

    // ────────────────────────────────────────────────────────
    //  매 프레임 그리기
    // ────────────────────────────────────────────────────────

	/**
	 * 배경 오버레이와 개체, 자막을 그린다.
	 */
	override fun drawElements() {
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
		};
	}

    /**
     * 등록된 모든 객체를 그린다 — 카메라 오프셋을 반영해서.
     *
     * 핵심 트릭: 객체의 월드 좌표에서 offset 을 잠깐 빼서 '화면 좌표' 처럼 만든 뒤
     *           draw() 시키고, 끝나면 원래 월드 좌표로 되돌린다.
     *
     * 이렇게 해야 서브클래스의 draw() 는 '자기 위치에 그냥 그려라' 만 구현하면 되고,
     *   카메라가 움직이든 말든 신경 쓸 필요가 없다.
	 *
	 * drawElements에서만 한 번 쓰이기 때문에 인라인 함수이다.
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
	fun updateCameraOffset() {
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
     * 구현 원리: 월드 좌표에서 카메라 offset 만큼 빼서 화면 좌표로 바꾼 뒤 drawText 호출.
	 *
	 * @param text				출력할 메시지
	 * @param x					X 위치
	 * @param y					Y 위치
	 * @param color				글자 색
	 * @param scale				글자 크기(배)
	 * @param width				텍스트 상자의 크기 (오른쪽이나 가운데 정렬 시 반드시 필요)
	 * @param align				글자 정렬(없으면 왼쪽 정렬)
	 * @param fixedWidthChars	고정폭으로 사용할 문자 (기본이 null이 아닌 이유는 실제로 빈 문자열이면 고정폭이 없다는 뜻)
	 * @param skipBatch			batch.begin()/end() 사이에서 사용할 경우 true
     */
    fun drawTextInWorld(text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float? = null, align: Int = Align.left, fixedWidthChars: String = "", skipBatch: Boolean = false) {
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

	// ────────────────────────────────────────────────────────
    //  자원 정리
    // ────────────────────────────────────────────────────────

    override fun dispose() {
        super.dispose();
        for(entity in entities) {
			if(entity is InventoryEntity)
				for(item in entity.getInventory())
					item.destroy();
			if(entity is Container)
				entity.containedItem?.destroy();
            entity.dispose();
		}
		entities.clear();
    }
}
