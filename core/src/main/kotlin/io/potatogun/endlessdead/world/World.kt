package io.potatogun.endlessdead.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
abstract class World(game: EndlessDead, val width: Float = game.screenWidth, val height: Float = game.screenHeight) : Screen(game) {
	// OrthographicCamera: 원근 없이(평행 투영) 2D 좌표를 그대로 그려주는 카메라.
    private val camera = OrthographicCamera();
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
	// 자막 타이머 관련 필드들
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;

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
	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		setCameraCenter();
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
	 * 월드의 배경은 일반적인 화면(스크린)의 배경과 다르게 카메라의 위치에 따라 달라지기 때문에
	 * 하위 클래스는 drawWorldBackground에서 구현해야 한다.
	 */
	final override fun drawBackground() {
		// 카메라 위치에 상대적으로 맞추기 위해 좌표계를 카메라로 변경
		batch.projectionMatrix = camera.combined;
		// 월드의 배경을 그린다.
        drawWorldBackground();
		// 다시 화면의 좌표계로 변경한다.
		batch.projectionMatrix = screenProjectionMatrix;
	}

	/**
	 * 월드의 배경을 그린다.
	 */
	abstract fun drawWorldBackground();

	/**
	 * 배경 오버레이와 개체, 자막을 그린다.
	 */
	override fun drawElements() {
		// 개체를 플레이어 위치(카메라 위치)에 상대적으로 맞추기 위해 좌표계를 카메라로 변경
		batch.projectionMatrix = camera.combined;
		// 개체들을 그린다.
        drawEntities();
		// 다시 화면의 좌표계로 변경하여 HUD나 미터기를 그릴 준비를 한다.
		batch.projectionMatrix = screenProjectionMatrix;

		// 자막이 있으면 표시
		if(subtitlesTimer > 0f) subtitlesMessage?.let {
			drawText(
				text = it,
				x = 0f,
				y = 20f,
				color = subtitlesColor,
				scale = 1.0f,
				width = game.screenWidth,
				align = Align.center,
				skipBatch = true
			);
		};
	}

    /**
     * 등록된 모든 객체를 그린다.
     *
     * 이렇게 해야 서브클래스의 draw() 는 '자기 위치에 그냥 그려라' 만 구현하면 되고,
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
	fun updateCameraOffset() {
        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
		// game.screenWidth는 Graphics#getWidth 메쏘드를 호출하므로 반복된 함수 호출 오버헤드를 줄이기 위해 미리 저장해둔다.
		val screenWidth = game.screenWidth;
		val screenHeight = game.screenHeight;
        offsetX = player.x.coerceIn(screenWidth / 2f, width - screenWidth / 2f);
        offsetY = player.y.coerceIn(screenHeight / 2f, height - screenHeight / 2f);
		camera.update();
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
	 * @param fixedWidthChars	고정폭으로 사용할 문자 (기본이 null이 아닌 이유는 실제로 빈 문자열이면 고정폭이 없다는 뜻)
	 * @param skipBatch			batch.begin()/end() 사이에서 사용할 경우 true
     */
    fun drawTextInWorld(text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float? = null, align: Int = Align.left, fixedWidthChars: String = "", skipBatch: Boolean = false) {
		batch.projectionMatrix = camera.combined;
        drawText(text, x, y, color, scale, width, align, fixedWidthChars, skipBatch);
		batch.projectionMatrix = screenProjectionMatrix;
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
