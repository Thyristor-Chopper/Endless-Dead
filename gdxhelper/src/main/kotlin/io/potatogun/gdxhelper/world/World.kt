package io.potatogun.gdxhelper.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.WorldViewer;
import io.potatogun.gdxhelper.util.EntityManager;
import io.potatogun.gdxhelper.util.SpatialHashGrid;
import io.potatogun.gdxhelper.util.weakMutableSetOf;
import io.potatogun.gdxhelper.world.Freezable;

import java.util.Collections;

/**
 * 게임 내 월드 = '월드 하나' 의 추상 기본 클래스.
 * '월드'의 개념에 맞게 플레이어나 적 등의 개체 등을 추가한다.
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
 * @property width  월드 전체 너비 (JvmField이 있지만 빌드 후 Fernflower로 자바로 디컴파일하여 null이 불가능한 원시 float임을 확인함.)
 * @property height 월드 전체 높이 (위와 동일)
 */
abstract class World(@JvmField val width: Float, @JvmField val height: Float) {
	/**
	 * 원근 없이(평행 투영) 2D 좌표를 그대로 그려주는 카메라
	 */
	private val camera = OrthographicCamera();
	/**
	 * 이미지(Texture)와 글자를 화면에 찍어주는 도구
	 */
	@JvmField protected val batch = SpriteBatch();  // @JvmField가 있지만 protected라 외부 자바 클래스에서 접근하라고 있는 게 아니기 때문에 캠슐화가 많이 깨지지는 않는 것 같아 성능을 위해서 씀.
	/**
	 * 월드의 기본 글꼴
	 *   월드 구현체에서 다른 글꼴을 사용할 수도 있으므로 open이다.
	 */
	protected open val font = BitmapFont();
	/**
	 * 월드를 보여주는 스크린. 만약 이 월드를 띄우는 뷰어가 없으면 null일 수도 있음에 주의
	 */
	val viewer: WorldViewer?
		inline get() = WorldViewer.getViewerByWorld(this);
	// 카메라 오프셋 — 월드의 어느 지점이 화면 중앙에 오는지.
	//   이 두 값만 바꾸면 카메라가 움직이는 효과가 난다.
	/**
	 * 카메라의 X 오프셋
	 */
	var offsetX: Float
		get() = camera.position.x
		protected set(value) { camera.position.x = value };
	/**
	 * 카메라의 Y 오프셋
	 */
	var offsetY: Float
		get() = camera.position.y
		protected set(value) { camera.position.y = value };
	/**
	 * 등록된 개체 목록
	 *   등록된 객체들만 update/draw된다.
	 *
	 * 자바에서도 world.entities.add()로 자연스럽게 호출하기 위해 @JvmField
	 */
	@JvmField val entities: EntityManager = SpatialHashGrid(128f);

	init {
		instances.add(this);
		setCameraCenter();
	}

	/**
	 * 카메라를 '왼쪽 아래 = (0,0), 오른쪽 위 = (screenWidth, screenHeight)'로 설정.
	 */
	private inline fun setCameraCenter() {
		// false 인자는 y 축을 위로(수학 좌표계처럼) 둔다는 뜻.
		camera.setToOrtho(false, Window.width, Window.height);
	}

	/**
	 * 월드 내 모든 개체 목록을 가져온다.
	 */
	inline fun getEntities(): List<Entity> = entities.getAll();

	/**
	 * 등록된 모든 개체에게 'update(delta) 한 프레임 진행'을 시킨다.
	 *   update 내에서만 한 번 쓰이기 때문에 inline이다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	private inline fun updateEntities(delta: Float) {
		for(entity in getEntities()) {
			if(this !is Freezable || !this.isFrozen || entity.isUpdatableWhileFrozen)
				entity.update(delta);
			entity.forceUpdate(delta);
		}
	}

	// ────────────────────────────────────────────────────────
	//  콜백 함수
	// ────────────────────────────────────────────────────────

	/**
	 * 창 크기 조절 시 호출된다.
	 *
	 * @param width  새 창 너비
	 * @param height 새 창 높이
	 */
	open fun onResize(width: Int, height: Int) {
		setCameraCenter();
		updateCamera();
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 로직
	// ────────────────────────────────────────────────────────

	/**
	 * 매 프레임 게임 로직 — 서브클래스가 override해서 자기 게임 로직을 넣는 곳.
	 *
	 * 기본 구현은 가장 단순한 '갱신 → 정리' 시나리오를 보여준다:
	 *   ① updateEntities(delta) — 각 객체가 자기 위치 갱신
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	open fun update(delta: Float) {
		updateEntities(delta);
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 그리기
	// ────────────────────────────────────────────────────────

	// 상속받은 자식 월드에서 추가적으로 그려야 할 것이 있다면 이 메쏘드 대신
	//   drawElements를 override할 것.
	internal fun render() {
		batch.begin();
		drawBackground();
		drawElements();
		batch.end();
	}

	/**
	 * 배경을 그리는 자리 — 모든 서브클래스가 반드시 구현해야 한다.
	 *
	 * 'abstract'인 이유:
	 *   기본 동작('아무것도 안 함')이 의미 있지 않다. 게임마다 배경은 다르고,
	 *   '배경이 없다'는 결정도 명시적으로 내려야 한다고 본다. 그래서 강제 구현.
	 *   (검은 배경을 원하면 그냥 비어있는 함수로 override하면 됨)
	 *
	 *   참고: update()는 abstract가 아닌 open이다 — 거기엔 쓸 만한 default가
	 *   존재하기 때문. 'default가 의미 있는가?' 가 abstract / open을 가르는 기준.
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
		// Window.width는 private set로 @JvmField가 불가능하여 내부적으로 getter 호출이 발생하여
		//   반복된 함수 호출 오버헤드를 줄이기 위해 미리 저장해둔다.
		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		val offsetX = this.offsetX;
		val offsetY = this.offsetY;

		for(entity in getEntities()) {
			// 보이는 개체만 그리기 (자원 낭비 감소)
			val maxEntityLength = Utils.max2(entity.width, entity.height);
			val entityX = entity.x;
			val entityY = entity.y;
			if(entityX >= offsetX - halfScreenWidth - maxEntityLength && entityX <= offsetX + halfScreenWidth + maxEntityLength && entityY >= offsetY - halfScreenHeight - maxEntityLength && entityY <= offsetY + halfScreenHeight + maxEntityLength)
				entity.draw(batch);
		}
	}

	/**
	 * 카메라를 갱신하고 월드에 따라 override하여 투영 좌표를 원하는 위치로 이동한다.
	 */
	open fun updateCamera() {
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
	 * @param text  출력할 메시지
	 * @param x     X 위치
	 * @param y     Y 위치
	 * @param color 글자 색
	 * @param scale 글자 크기(배)
	 * @param width 텍스트 상자의 크기, 0은 자동 (오른쪽이나 가운데 정렬 시 반드시 필요, 이때는 0 불가)
	 * @param align 글자 정렬(없으면 왼쪽 정렬)
	 */
	@JvmOverloads fun drawText(text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float = 0f, align: Int = Align.left) {
		Utils.drawText(batch, font, text, x, y, color, scale, width, align);
	}

	// ────────────────────────────────────────────────────────
	//  자원 정리
	// ────────────────────────────────────────────────────────

	open fun dispose() {
		batch.dispose();
		font.dispose();
		entities.dispose();
	}

	companion object {
		// 생성된 모든 인스턴스를 관리하는 목록이다. 생성자에서 자동으로 추가한다. 누가 설마 자바 unsafe의 allocateInstance를 쓰진 않겠지
		//   게임 dispose 시 사용된다.
		private val instances = weakMutableSetOf<World>();

		internal fun disposeAllWorlds() {
			for(world in instances)
				try {
					world.dispose();
				} catch(e: IllegalArgumentException) {}
		}
	}
}
