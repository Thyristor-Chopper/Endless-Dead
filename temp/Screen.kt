package io.potatogun.gdxhelper.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputListener as GdxInputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget as GdxWidget;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.IdentitySet;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.potatogun.gdxhelper.InputListeners;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.collections.toArray;
import io.potatogun.gdxhelper.util.ArraySuppliers;
import io.potatogun.gdxhelper.util.InputListener;
import io.potatogun.gdxhelper.util.JavaOnly;
import io.potatogun.gdxhelper.util.Updatable;
import io.potatogun.gdxhelper.util.drawText;
import io.potatogun.gdxhelper.widget.Widget;

/**
 * 게임 내 화면을 구현한다.
 *   안에는 배경과 위젯(컨트롤)을 추가할 수 있다.
 *
 * @constructor Named argument를 쓸 수 있는 코틀린용 생성자
 * @property font   화면 기본 글꼴 - 개발자가 수동으로 dispose해야 한다.
 */
abstract class Screen(font: BitmapFont? = null) : ScreenAdapter(), InputListener, Updatable {
	/**
	 * 이미지(Texture)와 글자를 화면에 찍어주는 도구.
	 *
	 * 배경 그리기·게임 객체·텍스트 모두 이 batch 하나로 처리한다.
	 */
	@JvmField protected val batch = SpriteBatch();
	/**
	 * 화면의 글꼴
	 */
	@JvmField protected val font: BitmapFont;
	private val autoDisposeFont: Boolean;
	/**
	 * 등록된 위젯들
	 */
	private val widgets = IdentitySet<Widget>();
	/**
	 * 오버레이 위에 표시되는 위젯
	 */
	private val overlayWidgets = IdentitySet<Widget>(16);
	private val listenableWidgets = IdentitySet<Widget>(16);
	private val iterableWidgets = GdxArray<Widget>(false, 16, ArraySuppliers.widget);
	private val gdxWidgets = IdentitySet<GdxWidget>(16);
	private val iterableGdxWidgets = GdxArray<Widget>(false, 16, ArraySuppliers.gdxWidget);

	/**
	 * 설정 빌더를 이용하여 화면을 생성한다.
	 *
	 * @constructor 자바용 생성자
	 * @param settings 스크린 옵션
	 */
	@OptIn(JavaOnly::class)
	@SinceKotlin("9999.9")  // 여기서 @JavaOnly를 쓰면 코틀린 내에서 그냥 Screen()를 호출할 때 이 생성자를 호출하려고 시도하면서 에러 남...
	@JvmOverloads
	constructor(settings: Properties = Properties()) : this(settings.font);

	init {
		if(font == null) {
			this.font = BitmapFont();
			autoDisposeFont = true;
		} else {
			this.font = font;
			autoDisposeFont = false;
		}

		updateProjectionMatrix();
		InputListeners.register(this);
	}

	/**
	 * 위젯을 화면에 추가한다.
	 *
	 * @param widget 추가할 위젯 객체
	 * @return 성공 여부 (존재하거나 위젯의 자원이 해제된 상태이면 실패)
	 */
	fun addWidget(widget: Widget): Boolean {
		if(widget.isDisposed) return false;
		if(widgets.contains(widget)) return false;
		widgets.add(widget);
		if(widget is InputListener)
			listenableWidgets.add(widget);
		return true;
	}

	/**
	 * 오버레이 위에 그려지는 위젯을 화면에 추가한다.
	 *
	 * @param widget 추가할 위젯 객체
	 * @return 성공 여부 (이미 식별자가 존재하거나 위젯의 자원이 해제된 상태이면 실패)
	 */
	fun addOverlayWidget(widget: Widget): Boolean {
		if(!addWidget(widget)) return false;
		overlayWidgets.add(widget);
		return true;
	}

	/**
	 * 위젯을 화면에서 제거한다.
	 *
	 * @param widget  제거할 위젯
	 * @param dispose 자원 해제 여부
	 * @return 성공 여부
	 */
	@JvmOverloads fun removeWidget(widget: Widget, dispose: Boolean = true): Boolean {
		if(!widgets.remove(widget)) return false;
		overlayWidgets.remove(widget);
		listenableWidgets.remove(widget);
		if(dispose)
			widget.dispose();
		return true;
	}

	/**
	 * 모든 위젯 목록의 사본
	 *
	 * @return 위젯들의 배열
	 */
	fun getWidgets(): GdxArray<Widget> = widgets.toArray();

	/**
	 * 모든 위젯 목록의 사본
	 *
	 * @param output 위젯들을 담을 배열
	 */
	fun getWidgets(output: GdxArray<Widget>) {
		widgets.toArray(output);
	}

	/**
	 * Screen2D 위젯을 화면에 추가한다.
	 *
	 * @param widget 추가할 위젯 객체
	 * @return 성공 여부 (이미 식별자가 존재하거나 위젯의 자원이 해제된 상태이면 실패)
	 */
	fun addWidget(widget: GdxWidget): Boolean {
		if(gdxWidgets.contains(widget)) return false;
		widgets.add(widget);
		return true;
	}

	/**
	 * Screen2D 위젯을 화면에서 제거한다.
	 *
	 * @param widget 제거할 위젯
	 * @return 성공 여부
	 */
	fun removeWidget(widget: GdxWidget): Boolean = widgets.remove(widget);

	@JvmSynthetic internal fun updateProjectionMatrix() {
		batch.projectionMatrix.setToOrtho2D(0f, 0f, Window.width, Window.height);
	}

	@JvmSynthetic internal fun updateAll(delta: Float) {
		update(delta);
		updateGdxWidgets(delta);
		updateWidgets(delta);
	}

	/**
	 * 매 프레임 화면 로직
	 *
	 * 서브클래스가 override해서 화면 로직을 넣는 곳이다.
	 *   아무 것도 안 할 수도 있으니 기본은 빈 함수이다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	override fun update(delta: Float) {}

	private inline fun updateWidgets(delta: Float) {  // update에서만 한 번 쓰이므로 인라인 함수
		widgets.toArray(iterableWidgets);
		for(i in 0 until iterableWidgets.size) {
			val widget = iterableWidgets.items[i];
			widget.update(delta);
		}
		iterableWidgets.clear();
	}

	private inline fun updateGdxWidgets(delta: Float) {  // update에서만 한 번 쓰이므로 인라인 함수
		gdxWidgets.toArray(iterableGdxWidgets);
		for(i in 0 until iterableGdxWidgets.size) {
			val widget = iterableGdxWidgets.items[i];
			widget.act(delta);
		}
		iterableGdxWidgets.clear();
	}

	// gdxhelper의 Game 객체가 아닌 LibGDX 자체의 Game 객체에서 사용할 때 대비
	final override fun render(delta: Float) {
		updateAll(delta);
		render();
	}

	@JvmSynthetic internal fun render() {
		// 1) 이전 프레임의 잔상 지우기 (검은색으로 채움)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, Window.realWidth, Window.realHeight);

		// 2) 그리기 — SpriteBatch는 begin()/end() 사이에서만 동작한다.
		batch.begin();
		drawBackground();
		drawContents();
		drawGdxWidgets();
		drawWidgets();
		drawOverlay();
		drawOverlayWidgets();
		batch.end();
	}

	/**
	 * 배경을 그리는 자리 — 모든 서브클래스가 반드시 구현해야 한다. (검은 배경을 원하면 그냥 비어있는 함수로 override하면 됨)
	 */
	protected abstract fun drawBackground();

	/**
	 * 그 외 하위 클래스에서 배경과 위젯(컨트롤) 사이에 그려야 할 것들
	 */
	protected open fun drawContents() {}

	/**
	 * 스크린에 등록된 위젯(컨트롤)들을 그린다.
	 *
	 * @return 보이는(실제로 그린) 위젯 수
	 */
	private inline fun drawWidgets(): Int {  // render에서만 한 번 쓰이므로 인라인이다.
		var count = 0;
		val iterator = widgets.iterator();
		while(iterator.hasNext) {
			val widget = iterator.next();
			if(widget.isVisible && !overlayWidgets.contains(widget)) {
				widget.draw(batch);
				count++;
			}
		}
		return count;
	}

	/**
	 * 스크린에 등록된 위젯(컨트롤) 중 오버레이 위에 그려지는 위젯을 그린다.
	 *
	 * @return 보이는(실제로 그린) 위젯 수
	 */
	private inline fun drawOverlayWidgets(): Int {  // render에서만 한 번 쓰이므로 인라인이다.
		var count = 0;
		val iterator = overlayWidgets.iterator();
		while(iterator.hasNext) {
			val widget = iterator.next();
			if(widget.isVisible) {
				widget.draw(batch);
				count++;
			}
		}
		return count;
	}

	private inline fun drawGdxWidgets(): Int {  // render에서만 한 번 쓰이므로 인라인이다.
		var count = 0;
		val iterator = gdxWidgets.iterator();
		while(iterator.hasNext) {
			val widget = iterator.next();
			if(widget.isVisible) {
				widget.draw(batch, 1f);
				count++;
			}
		}
		return count;
	}

	/**
	 * 스크린의 모든 것 위에 그려야 할 오버레이가 있다면 여기서 그린다.
	 */
	protected open fun drawOverlay() {}

	/**
	 * 스크린의 addWidget로 직접 등록하지 않은 위젯(컨트롤)을 수동으로 그린다.
	 *
	 * @param widget 그릴 위젯
	 * @return isVisible이 false라 그리지 않았으면 false
	 */
	fun drawWidget(widget: Widget): Boolean {
		if(!widget.isVisible) return false;
		widget.draw(batch);
		return true;
	}

	/**
	 * 화면 좌표에 텍스트 그리기.
	 *
	 * 카메라가 어디로 움직이든 화면상 같은 위치에 고정된다.
	 *   → 점수, HP, 남은 시간 같은 UI에 적합.
	 *
	 * 주의: 화면 y 축은 위쪽이 크다. 화면 '위쪽'에 글자를 쓰려면 y = screenHeight-10 처럼.
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
		drawText(batch, font, text, x, y, color, scale, width, align);
	}

	override fun onScroll(amountX: Float, amountY: Float): Boolean = forEachListeners { it.onScroll(amountX, amountY) };

	override fun onKeyDown(code: Int): Boolean = forEachListeners { it.onKeyDown(code) };

	override fun onKeyUp(code: Int): Boolean = forEachListeners { it.onKeyUp(code) };

	override fun onMouseMove(x: Int, y: Int): Boolean = forEachListeners { it.onMouseMove(x, y) };

	override fun onTouchDrag(x: Int, y: Int, pointer: Int): Boolean = forEachListeners { it.onTouchDrag(x, y, pointer) };

	override fun onTouchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean = forEachListeners { it.onTouchDown(x, y, pointer, button) };

	override fun onTouchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean = forEachListeners { it.onTouchUp(x, y, pointer, button) };

	override fun onTouchCancel(x: Int, y: Int, pointer: Int, button: Int): Boolean = forEachListeners { it.onTouchCancel(x, y, pointer, button) };
	
	override fun onKeyType(char: Char): Boolean = forEachListeners { it.onKeyType(char) };

	private inline fun forEachListeners(crossinline callback: (InputListener) -> Boolean): Boolean {
		var processed = false;
		listenableWidgets.toArray(iterableWidgets);
		for(i in 0 until iterableWidgets.size) {
			val widget = iterableWidgets.items[i];
			if(widget !is InputListener) continue;
			processed = callback(widget) || processed;
		}
		iterableWidgets.clear();
		return processed;
	}

	/**
	 * LibGDX가 화면을 바꾸거나 앱을 종료할 때 자원을 해제한다.
	 *   GPU 메모리에 올라간 것들은 수동으로 dispose 해줘야 한다.
	 */
	override fun dispose() {
		InputListeners.unregister(this);
		batch.dispose();
		if(autoDisposeFont)
			font.dispose();
		val iterator = widgets.iterator();
		while(iterator.hasNext) {
			val widget = iterator.next();
			widget.dispose();
		}
		widgets.clear();
	}

	/**
	 * 스크린 옵션 (자바용)
	 */
	@JavaOnly open class Properties {
		@get:JvmSynthetic internal var font: BitmapFont? = null
			private set;

		/**
		 * 글꼴을 지정한다.
		 *
		 * @param font 글꼴
		 * @return 옵션 객체 자신
		 */
		fun font(font: BitmapFont): Properties {
			this.font = font;
			return this;
		}
	}
}
