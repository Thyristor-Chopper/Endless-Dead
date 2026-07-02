package io.potatogun.gdxhelper.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.ObjectSet;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.WorldProjector;
import io.potatogun.gdxhelper.util.EntityManager;
import io.potatogun.gdxhelper.util.SpatialGrid;
import io.potatogun.gdxhelper.util.weakMutableSetOf;
import io.potatogun.gdxhelper.world.Freezable;

import java.util.Collections;

/**
 * 게임 내 월드 = '월드 하나'의 추상 기본 클래스.
 *   '월드'의 개념에 맞게 플레이어나 적 등의 개체 등을 추가한다.
 *
 * @constructor Named argument를 쓸 수 있는 코틀린용 생성자
 * @property width    월드 전체 너비
 * @property height   월드 전체 높이
 * @property camera   월드의 카메라
 * @property font     월드의 기본 글꼴
 * @property tileSize 공간 분할 격자 개체 관리자의 격자 크기
 */
abstract class World(@JvmField val width: Float, @JvmField val height: Float, camera: Camera = OrthographicCamera(), font: BitmapFont = BitmapFont(), tileSize: Float = 64f) {
	/**
	 * 월드를 보여주는 카메라
	 */
	private val camera = camera;
	/**
	 * 이미지(Texture)와 글자를 화면에 찍어주는 도구
	 */
	@JvmField protected val batch = SpriteBatch();
	/**
	 * 월드의 기본 글꼴
	 */
	@JvmField protected val font = font;
	/**
	 * 월드를 보여주는 스크린. 만약 이 월드를 띄우는 뷰어가 없으면 null일 수도 있음에 주의
	 */
	val projector: WorldProjector?
		inline get() = WorldProjector.getProjectorByWorld(this);
	// 카메라 오프셋 — 월드의 어느 지점이 화면 중앙에 오는지.
	//   이 두 값만 바꾸면 카메라가 움직이는 효과가 난다.
	/**
	 * 카메라의 X 오프셋
	 */
	var offsetX: Float
		get() = camera.position.x
		protected set(value) {
			camera.position.x = value;
			updateProjectionMatrix();
		};
	/**
	 * 카메라의 Y 오프셋
	 */
	var offsetY: Float
		get() = camera.position.y
		protected set(value) {
			camera.position.y = value;
			updateProjectionMatrix();
		};
	/**
	 * 등록된 개체 목록
	 *   등록된 객체들만 update/draw된다.
	 *
	 * 자바에서도 world.entities.add()로 자연스럽게 호출하기 위해 @JvmField
	 */
	@JvmField val entities: EntityManager = SpatialGrid(this, tileSize);

	/**
	 * 설정 빌더를 사용하여 월드를 생성한다.
	 *
	 * @constructor 자바용 생성자
	 * @param width    월드 전체 너비
	 * @param height   월드 전체 높이
	 * @param settings 월드 설정
	 */
	@JvmOverloads constructor(width: Float, height: Float, settings: Properties = Properties()) : this(width, height, settings.camera!!, settings.font!!, settings.tileSize);

	init {
		updateViewport();
		if(camera is OrthographicCamera)
			camera.setToOrtho(false);  // false 인자는 y 축을 위로(수학 좌표계처럼) 둔다는 뜻.

		undisposed.add(this);
	}

	// ────────────────────────────────────────────────────────
	//  콜백 함수
	// ────────────────────────────────────────────────────────

	internal fun updateCamera() {
		updateViewport();
		updateOffset();
		updateProjectionMatrix();
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 로직
	// ────────────────────────────────────────────────────────

	/**
	 * 매 프레임 게임 로직 — 서브클래스가 override해서 자기 게임 로직을 넣는 곳.
	 *
	 * 기본 구현은 가장 단순한 '갱신 → 정리' 시나리오를 보여준다:
	 *   ① entities.update(delta) — 각 객체가 자기 위치 갱신
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	open fun update(delta: Float) {
		entities.update(delta);
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
	 * 배경을 그리는 자리 — 모든 서브클래스가 반드시 구현해야 한다. (월드 뷰어의 배경을 그대로 쓰려면 그냥 비어있는 함수로 override하면 됨)
	 */
	protected abstract fun drawBackground();

	/**
	 * 월드에서 그려야 할 요소(등록된 개체 등)를 그린다.
	 *   월드에 벽이나 그런 걸 그릴 때도 여기서 override해서 그리면 된다.
	 */
	protected open fun drawElements() {
		entities.draw(batch);
	}

	/**
	 * 카메라를 갱신한다. 여러 번 쓰이지만 두 줄뿐이라 인라인... 나중에 함수가 더 커지면 인라인 해제
	 */
	private inline fun updateProjectionMatrix() {
		camera.update();
		batch.projectionMatrix = camera.combined;
	}

	/**
	 * 월드에 따라 override하여 투영 좌표를 의도에 맞는 위치로 이동한다.
	 */
	open fun updateOffset() {}

	/**
	 * 화면 크기에 맞게 카메라를 갱신한다.
	 */
	private inline fun updateViewport() {
		camera.viewportWidth = Window.width;
		camera.viewportHeight = Window.height;
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
		undisposed.remove(this);
		batch.dispose();
		font.dispose();
		entities.dispose();
	}

	/**
	 * 월드 옵션 (자바용)
	 */
	open class Properties {
		internal var camera: Camera? = null
			get() {
				if(field == null)
					field = OrthographicCamera();
				return field;
			}
			private set;
		internal var font: BitmapFont? = null
			get() {
				if(field == null)
					field = BitmapFont();
				return field;
			}
			private set;
		internal var tileSize = 64f
			private set;

		/**
		 * 카메라를 지정한다.
		 *
		 * @param camera 카메라
		 * @return       옵션 객체 자신
		 */
		fun camera(camera: Camera): Properties {
			this.camera = camera;
			return this;
		}

		/**
		 * 글꼴을 지정한다.
		 *
		 * @param font 글꼴
		 * @return     옵션 객체 자신
		 */
		fun font(font: BitmapFont): Properties {
			this.font = font;
			return this;
		}

		/**
		 * 격자 개체 관리자의 타일 크기를 지정한다.
		 *
		 * @param tileSize 타일 크기
		 * @return         옵션 객체 자신
		 */
		fun tileSize(tileSize: Float): Properties {
			this.tileSize = tileSize;
			return this;
		}
	}

	companion object {
		// 생성된 모든 인스턴스를 관리하는 목록이다. 생성자에서 자동으로 추가한다. 누가 설마 자바 unsafe의 allocateInstance를 쓰진 않겠지
		//   게임 dispose 시 사용된다.
		private val undisposed = ObjectSet<World>(4);

		/**
		 * 수동 자원 해제되지 않은 월드들을 일괄 dispose한다.
		 */
		internal fun disposeUndispoed() {
			val iterator = undisposed.iterator();
			while(iterator.hasNext) {
				val world = iterator.next();
				world.dispose();
				iterator.remove();
			}
		}
	}
}
