package io.potatogun.gdxhelper.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.screen.Screen;
import io.potatogun.gdxhelper.widget.Widget;

/**
 * 게임 내 화면을 구현한다.
 *   안에는 배경과 위젯(컨트롤)을 추가할 수 있다.
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
 *  보통 override 하는 것:
 *   ▸ drawBackground(batch) — 자기 배경 그리기 (필수, abstract)
 *   ▸ update(delta)         — 자기 게임 로직 (대부분 override 함)
 *   ▸ render(delta)         — 객체 위에 텍스트/HUD 추가 그리기 (선택)
 */
abstract class Screen : ScreenAdapter() {
	/**
	 * 이미지(Texture)와 글자를 화면에 찍어주는 도구.
	 *   배경 그리기·게임 객체·텍스트 모두 이 batch 하나로 처리한다.
	 */
	@JvmField protected val batch = SpriteBatch();  // @JvmField가 있지만 protected라 외부 자바 클래스에서 접근하라고 있는 게 아니기 때문에 캡슐화가 많이 깨지지는 않는 것 같아 성능을 위해서 씀.
	/**
	 * 화면의 기본 글꼴
	 *   화면 구현체에서 다른 글꼴을 사용할 수도 있으므로 open이다.
	 */
	protected open val font = BitmapFont();
	/**
	 * 등록된 위젯들
	 */
	private val widgets = mutableMapOf<String, Widget>();

	// ────────────────────────────────────────────────────────
	//  위젯 객체 관리
	// ────────────────────────────────────────────────────────

	/**
	 * 위젯을 화면에 추가
	 *
	 * @param id     위젯의 식별자
	 * @param widget 추가할 위젯 객체
	 * @return       성공 여부 (이미 식별자가 존재하면 실패)
	 */
	fun addWidget(id: String, widget: Widget): Boolean {
		if(widgets.containsKey(id)) return false;
		widgets[id] = widget;
		return true;
	}

	/**
	 * 위젯을 화면에서 제거
	 *
	 * @param id 위젯의 식별자
	 * @return   성공 여부
	 */
	fun removeWidget(id: String): Boolean {
		val widget: Widget? = widgets[id];
		if(widget == null) return false;
		widgets.remove(id);
		widget.dispose();
		return true;
	}

	/**
	 * 위젯을 식별자로 가져오기
	 *
	 * @param id 가져올 위젯의 식별자
	 * @return   해당 위젯
	 * @throws NoSuchElementException 지정한 식별자의 위젯이 없는 경우
	 */
	fun getWidget(id: String): Widget = widgets[id] ?: throw NoSuchElementException("invalid widget ID");

	/**
	 * 모든 위젯 목록 (읽기 전용)
	 *
	 * @return 위젯들의 컬렉션
	 */
	fun getWidgets(): Collection<Widget> = widgets.values;

	// ────────────────────────────────────────────────────────
	//  콜백 함수
	// ────────────────────────────────────────────────────────

	override fun resize(width: Int, height: Int) {
		batch.projectionMatrix.setToOrtho2D(0f, 0f, width.toFloat(), height.toFloat());
	}

	// ────────────────────────────────────────────────────────
	//  매 프레임 로직
	// ────────────────────────────────────────────────────────

	/**
	 * 매 프레임 화면 로직 — 서브클래스가 override해서 화면 로직을 넣는 곳.
	 *   아무 것도 안 할 수도 있으니 기본은 빈 함수
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	protected open fun update(delta: Float) {}

	// ────────────────────────────────────────────────────────
	//  매 프레임 그리기
	// ────────────────────────────────────────────────────────

	/**
	 * LibGDX가 매 프레임 자동으로 호출.
	 *   기본 흐름: 화면 지우기 → 로직 업데이트 → 배경 → 객체.
	 *
	 * 서브클래스는 보통 update(delta)만 override 한다.
	 * HUD/텍스트를 그리려면 render(delta)도 override해서 super 호출 뒤에 그린다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	override fun render(delta: Float) {
		// 1) 이전 프레임의 잔상 지우기 (검은색으로 채움)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 2) 화면 로직 업데이트
		update(delta);

		// 3) 그리기 — SpriteBatch는 begin()/end() 사이에서만 동작한다.
		batch.begin();
		drawBackground();
		drawElements();
		drawWidgets();
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
	 *   존재하기 때문. 'default가 의미 있는가?'가 abstract / open을 가르는 기준.
	 */
	protected abstract fun drawBackground();

	/**
	 * 그 외 하위 클래스에서 배경과 위젯(컨트롤) 사이에 그려야 할 것들
	 */
	protected open fun drawElements() {}

	/**
	 * 스크린에 등록된 위젯(컨트롤)들을 그린다.
	 * render에서만 한 번 쓰이기 때문에 inline이다.
	 *
	 * @return 보이는(실제로 그린) 위젯 수
	 */
	private inline fun drawWidgets(): Int {
		var count = 0;
		for(widget in widgets.values)
			if(widget.isVisible) {
				widget.draw(batch);
				count++;
			}
		return count;
	}

	/**
	 * 스크린의 addWidget로 직접 등록하지 않은 위젯(컨트롤)을 수동으로 그린다.
	 *
	 * @param widget 그릴 위젯
	 * @return       isVisible이 false라 그리지 않았으면 false
	 */
	fun drawWidget(widget: Widget): Boolean {
		if(!widget.isVisible) return false;
		widget.draw(batch);
		return true;
	}

	// ────────────────────────────────────────────────────────
	//  텍스트 헬퍼
	// ────────────────────────────────────────────────────────

	/**
	 * 화면 좌표에 텍스트 그리기.
	 *
	 * 카메라가 어디로 움직이든 화면상 같은 위치에 고정된다.
	 *   → 점수, HP, 남은 시간 같은 UI 에 적합.
	 *
	 * 주의: 화면 y 축은 위쪽이 크다. 화면 '위쪽'에 글자를 쓰려면 y = screenHeight-10 처럼.
	 *
	 * @param text      출력할 메시지
	 * @param x         X 위치
	 * @param y         Y 위치
	 * @param color     글자 색
	 * @param scale     글자 크기(배)
	 * @param width     텍스트 상자의 크기 (오른쪽이나 가운데 정렬 시 반드시 필요)
	 * @param align     글자 정렬(없으면 왼쪽 정렬)
	 * @param skipBatch batch.begin()/end() 사이에서 사용할 경우 true
	 */
	@JvmOverloads fun drawText(text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float? = null, align: Int = Align.left, skipBatch: Boolean = false) {
		Utils.drawText(batch, font, text, x, y, color, scale, width, align, skipBatch);
	}

	// ────────────────────────────────────────────────────────
	//  자원 정리
	// ────────────────────────────────────────────────────────

	/**
	 * LibGDX가 화면을 바꾸거나 앱을 종료할 때 자원을 해제한다.
	 * GPU 메모리에 올라간 것들은 수동으로 dispose 해줘야 한다.
	 */
	override fun dispose() {
		batch.dispose();
		font.dispose();
		for(widget in widgets.values)
			widget.dispose();
		widgets.clear();
	}
}

// 확장 함수

/**
 * 위젯을 식별자로 가져오기 (안전판)
 *
 * @param id 가져올 위젯의 식별자
 * @return   해당 위젯(없으면 null)
 */
inline fun Screen.getWidgetOrNull(id: String): Widget? {
	try {
		return this.getWidget(id);
	} catch(e: NoSuchElementException) {
		return null;
	}
}
