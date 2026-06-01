package io.potatogun.endlessdead.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.Entity;
import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Screen;
import io.potatogun.endlessdead.screen.WorldViewer;

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
 * @param viewer	월드를 보여주는 스크린
 * @param width		월드 전체 너비
 * @param height	월드 전체 높이
 */
abstract class World(val game: EndlessDead, val viewer: WorldViewer, @JvmField val width: Float, @JvmField val height: Float) {
	// OrthographicCamera: 원근 없이(평행 투영) 2D 좌표를 그대로 그려주는 카메라.
    private val camera = OrthographicCamera();
	@JvmField protected val batch = SpriteBatch();
    @JvmField protected val font = BitmapFont();
	/**
	 * 이 월드의 플레이어
	 */
	abstract val player: Player;
    /**
	 * 카메라 오프셋 — 월드의 어느 지점이 화면 중앙에 오는지.
     *   이 두 값만 바꾸면 카메라가 움직이는 효과가 난다.
	 */
    var offsetX: Float
		get() = camera.position.x
		private set(value) { camera.position.x = value };
    var offsetY: Float
		get() = camera.position.y
		private set(value) { camera.position.y = value };
    // 등록된 객체들만 update/draw 된다.
    // private 으로 감춘 이유: 외부가 직접 add/remove 하면
    //   '순회 중 삭제' 같은 버그가 나기 쉽다. addEntity(), removeEntity()라는 공식 창구만 허용.
    //   (5주차에서 배운 캡슐화의 실제 사례)
    private val entities = mutableListOf<Entity>();

    init {
        setCameraCenter();
    }

	/**
	 * 카메라를 '왼쪽 아래 = (0,0), 오른쪽 위 = (screenWidth, screenHeight)'로 설정.
	 */
	private inline fun setCameraCenter() {
        // false 인자는 y 축을 위로(수학 좌표계처럼) 둔다는 뜻.
		camera.setToOrtho(false, game.screenWidth, game.screenHeight);
	}

    // ────────────────────────────────────────────────────────
    //  개체 관리
    // ────────────────────────────────────────────────────────

    /**
	 * 개체를 월드에 등록 — 이후부터 자동으로 update/draw 된다.
	 *
	 * @param entity 추가할 개체
	 */
    fun addEntity(entity: Entity) {
        entities.add(entity);
    }

    /**
	 * 특정 개체를 수동 제거. 보통은 isAlive=false 후 removeDead() 로 정리.
	 *
	 * @param entity 제거할 개체
	 * @return 성공 여부
	 */
    fun removeEntity(entity: Entity): Boolean {
		if(!entities.any { it === entity }) return false;
        entities.remove(entity);
		entity.dispose();
		return true;
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
     * 등록된 모든 개체에게 'update(delta) 한 프레임 진행' 을 시킨다.
	 *
	 * update 내에서만 한 번 쓰이기 때문에 inline이다.
     */
    private inline fun updateEntities(delta: Float) {
		for(entity in entities.shuffled()) {
			if(this !is Freezable || !this.isFrozen || entity.canUpdateWhileFrozen)
				entity.update(delta);
			entity.forceUpdate(delta);
		}
    }

    /**
     * isAlive가 false인 객체들을 한꺼번에 제거한다.
     *
     * update()에서 호출 — 상호작용 결과 죽음을 표시한 객체를 정리.
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
        for(entity in toRemove)
			removeEntity(entity);
    }

    // ────────────────────────────────────────────────────────
    //  콜백 함수
    // ────────────────────────────────────────────────────────

	/**
	 * 크기 조절 시 호출된다.
	 */
	internal fun resize(width: Int, height: Int) {
		setCameraCenter();
		updateCamera();
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
    internal open fun update(delta: Float) {
		updateEntities(delta);
		removeDead();
	}

    // ────────────────────────────────────────────────────────
    //  매 프레임 그리기
    // ────────────────────────────────────────────────────────

	internal fun render() {
		batch.begin();
		drawBackground();
		drawElements();
		batch.end();
	}

	/**
	 * 배경을 그리는 자리 — 모든 서브클래스가 반드시 구현해야 한다.
	 *
	 * 'abstract' 인 이유:
	 *   기본 동작('아무것도 안 함') 이 의미 있지 않다. 게임마다 배경은 다르고,
	 *   '배경이 없다'는 결정도 명시적으로 내려야 한다고 본다. 그래서 강제 구현.
	 *   (검은 배경을 원하면 그냥 비어있는 함수로 override 하면 됨)
	 *
	 *   참고: update() 는 abstract 가 아닌 open 이다 — 거기엔 쓸 만한 default 가
	 *   존재하기 때문. 'default 가 의미 있는가?' 가 abstract / open 을 가르는 기준.
	 *   (7주차 강의 포인트)
	 *
	 * @param batch 이미 begin() 된 SpriteBatch — 여기에 batch.draw(texture, ...)로 그린다.
	 *              begin/end 를 또 호출하면 안 된다.
	 */
	protected abstract fun drawBackground();

	/**
	 * 월드에서 그려야 할 요소(등록된 개체 등)를 그린다.
	 */
	protected open fun drawElements() {
		drawEntities();
	}

    /**
     * 등록된 모든 객체를 그린다.
     *
     * 이렇게 해야 서브클래스의 draw() 는 '자기 위치에 그냥 그려라'만 구현하면 되고,
     *   카메라가 움직이든 말든 신경 쓸 필요가 없다.
	 *
	 * drawElements에서만 한 번 쓰이기 때문에 인라인 함수이다.
     */
    private inline fun drawEntities() {
        for(entity in entities)
            entity.draw(batch);
    }

	/**
	 * 플레이어 위치에 따라 카메라 위치 변경
	 */
	fun updateCamera() {
        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
		// game.screenWidth는 Graphics#getWidth 메쏘드를 호출하므로 반복된 함수 호출 오버헤드를 줄이기 위해 미리 저장해둔다.
		val screenWidth = game.screenWidth;
		val screenHeight = game.screenHeight;
        offsetX = player.x.coerceIn(screenWidth / 2f, width - screenWidth / 2f);
        offsetY = player.y.coerceIn(screenHeight / 2f, height - screenHeight / 2f);
		camera.update();
		batch.projectionMatrix = camera.combined;
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
	 * @param text				출력할 메시지
	 * @param x					X 위치
	 * @param y					Y 위치
	 * @param color				글자 색
	 * @param scale				글자 크기(배)
	 * @param width				텍스트 상자의 크기 (오른쪽이나 가운데 정렬 시 반드시 필요)
	 * @param align				글자 정렬(없으면 왼쪽 정렬)
	 * @param skipBatch			batch.begin()/end() 사이에서 사용할 경우 true
     */
    fun drawText(text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float? = null, align: Int = Align.left, skipBatch: Boolean = false) {
		Utils.drawText(batch, font, text, x, y, color, scale, width, align, skipBatch);
    }

	// ────────────────────────────────────────────────────────
    //  자원 정리
    // ────────────────────────────────────────────────────────

    internal open fun dispose() {
		batch.dispose();
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
