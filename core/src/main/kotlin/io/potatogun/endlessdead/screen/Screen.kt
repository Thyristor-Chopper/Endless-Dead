package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.widget.Widget;

/**
 * 게임 내 화면을 구현한다.
 * 안에는 배경과 위젯(컨트롤)을 추가할 수 있다
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
 *
 * @param game 화면이 속한 게임
 */
abstract class Screen(@JvmField val game: EndlessDead) : ScreenAdapter() {
	// SpriteBatch: 이미지(Texture) 와 글자를 화면에 찍어주는 도구.
    //   배경 그리기·게임 객체·텍스트 모두 이 batch 하나로 처리한다.
    @JvmField protected val batch = SpriteBatch();
	// 기본 글꼴
    @JvmField protected val font = BitmapFont();
	// 등록된 위젯들
    private val widgets = mutableMapOf<String, Widget>();

    // ────────────────────────────────────────────────────────
    //  위젯 객체 관리
    // ────────────────────────────────────────────────────────

	/**
	 * 위젯을 화면에 추가
	 */
	fun addWidget(id: String, widget: Widget) {
		widgets[id] = widget;
	}

	/**
	 * 위젯을 화면에서 제거
	 *
	 * @return 성공 여부
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
	 */
	fun getWidget(id: String): Widget = widgets[id] ?: throw IllegalArgumentException("invalid widget ID");

	/**
	 * 모든 위젯 목록 (읽기 전용)
	 */
	fun getWidgets(): List<Widget> = widgets.values.toList();

    // ────────────────────────────────────────────────────────
    //  콜백 함수
    // ────────────────────────────────────────────────────────

	/**
	 * 크기 조절 시 호출된다.
	 */
	override fun resize(width: Int, height: Int) {
		batch.projectionMatrix.setToOrtho2D(0f, 0f, width.toFloat(), height.toFloat());
	}

	// ────────────────────────────────────────────────────────
    //  매 프레임 로직
    // ────────────────────────────────────────────────────────

	/**
     * 매 프레임 화면 로직 — 서브클래스가 override해서 화면 로직을 넣는 곳.
     */
	protected open fun update(delta: Float) {}

	// ────────────────────────────────────────────────────────
    //  매 프레임 그리기
    // ────────────────────────────────────────────────────────

    /**
     * LibGDX 가 매 프레임 자동으로 호출.
     *   기본 흐름: 화면 지우기 → 로직 업데이트 → 배경 → 객체.
     *
     * 서브클래스는 보통 update(delta) 만 override 한다.
     * HUD/텍스트를 그리려면 render(delta) 도 override 해서 super 호출 뒤에 그린다.
     */
	override fun render(delta: Float) {
		// 1) 이전 프레임의 잔상 지우기 (검은색으로 채움)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) 화면 로직 업데이트
        update(delta);

		// 3) 그리기 — SpriteBatch 는 begin()/end() 사이에서만 동작한다.
		batch.begin();
		drawBackground();
		drawElements();
		drawWidgets();
		batch.end();
	}

    /**
     * 배경을 그리는 자리 — 모든 서브클래스가 반드시 구현해야 한다.
     *
     * 'abstract' 인 이유:
     *   기본 동작('아무것도 안 함') 이 의미 있지 않다. 게임마다 배경은 다르고,
     *   '배경이 없다' 는 결정도 명시적으로 내려야 한다고 본다. 그래서 강제 구현.
     *   (검은 배경을 원하면 그냥 비어있는 함수로 override 하면 됨)
     *
     *   참고: update() 는 abstract 가 아닌 open 이다 — 거기엔 쓸 만한 default 가
     *   존재하기 때문. 'default 가 의미 있는가?' 가 abstract / open 을 가르는 기준.
     *   (7주차 강의 포인트)
     *
     * @param batch 이미 begin() 된 SpriteBatch — 여기에 batch.draw(texture, ...) 로 그린다.
     *              begin/end 를 또 호출하면 안 된다.
     */
    protected abstract fun drawBackground();

	/**
	 * 그 외 하위 클래스에서 배경과 위젯(컨트롤) 사이에 그려야 할 것들
	 */
	protected open fun drawElements() {}

	/**
	 * 화면 내 위젯(컨트롤)들을 그린다.
	 */
	private inline fun drawWidgets() {
		for(widget in widgets.values)
			if(widget.isVisible)
				widget.draw(batch);
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

    /**
     * LibGDX 가 화면을 바꾸거나 앱을 종료할 때 자원을 해제한다.
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
