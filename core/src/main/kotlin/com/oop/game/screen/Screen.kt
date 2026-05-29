package com.oop.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import com.oop.game.Updatable;
import com.oop.game.ZombieGame;
import com.oop.game.widget.Widget;

abstract class Screen(val game: ZombieGame) : ScreenAdapter(), Updatable {
	// OrthographicCamera: 원근 없이(평행 투영) 2D 좌표를 그대로 그려주는 카메라.
    protected val camera = OrthographicCamera();
	// SpriteBatch: 이미지(Texture) 와 글자를 화면에 찍어주는 도구.
    //   배경 그리기·게임 객체·텍스트 모두 이 batch 하나로 처리한다.
    protected val batch = SpriteBatch();
    protected val font = BitmapFont();
	// 등록된 위젯들
    private val widgets = mutableMapOf<String, Widget>();

    init {
        setCameraCenter();
    }
	
	private inline fun setCameraCenter() {
        // 카메라를 '왼쪽 아래 = (0,0), 오른쪽 위 = (screenWidth, screenHeight)' 로 설정.
        //   false 인자는 y 축을 위로(수학 좌표계처럼) 둔다는 뜻.
		camera.setToOrtho(false, game.screenWidth.toFloat(), game.screenHeight.toFloat());
	}

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
	fun getWidget(id: String): Widget {
		if(!(id in widgets)) throw IllegalArgumentException("invalid widget ID");
		return widgets[id]!!;
	}

    // ────────────────────────────────────────────────────────
    //  콜백 함수
    // ────────────────────────────────────────────────────────
	
	/**
	 * 크기 조절 시 호출된다.
	 */
	override fun resize(width: Int, height: Int) {
		game.screenWidth = width;
		game.screenHeight = height;
		setCameraCenter();
	}

    // ────────────────────────────────────────────────────────
    //  매 프레임 로직
    // ────────────────────────────────────────────────────────
	
	/**
     * 매 프레임 게임 로직 — 서브클래스가 override 해서 자기 게임 로직을 넣는 곳.
     *
     * 기본 구현은 가장 단순한 '갱신 → 정리' 시나리오를 보여준다:
     *   ① updateAllObjects(delta) — 각 객체가 자기 위치 갱신
     *   ② removeDead()            — isAlive=false 인 객체 제거
     *
     * 객체 간 상호작용(충돌·점수·생사 결정) 이 있는 게임이면 override 해서
     * 위 두 호출 사이에 그 로직을 끼워 넣는다 (ExampleWorld 참고).
     */
	override fun update(delta: Float) {}
	
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

        // 2) 카메라 상태 갱신 후, batch 에게 '이 카메라의 좌표계로 그려라' 알려줌
        camera.update();
        batch.projectionMatrix = camera.combined;

        // 3) 게임 로직 업데이트
        update(delta);
		
		// 4) 그리기 — SpriteBatch 는 begin()/end() 사이에서만 동작한다.
		batch.begin();
		drawBackground();
		drawElements(delta);
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
	protected open fun drawElements(delta: Float) {}
	
	/**
	 * 화면 내 위젯(컨트롤)들을 그린다.
	 */
	private inline fun drawWidgets() {
		for(widget in widgets.values)
			if(widget.visible)
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
     */
	fun drawText(
        text: String,
        x: Float,
        y: Float,
        color: Color = Color.WHITE,
        scale: Float = 1f,
		width: Float? = null,  // 오른쪽이나 가운데 정렬 시 필요
		align: Int? = null,  // 글자 정렬(없으면 왼쪽 정렬)
		fixedWidthChars: String = "",  // null이 아닌 이유는 실제로 빈 문자열이면 고정폭이 없다는 뜻
		skipBatch: Boolean = false
    ) {
        if(!skipBatch) batch.projectionMatrix = camera.combined;
		font.setFixedWidthGlyphs(fixedWidthChars);
        font.color = color;
        font.data.setScale(scale);
        if(!skipBatch) batch.begin();
		val boxWidth: Float? = width;
		val textAlign: Int? = align;
		if(boxWidth != null && textAlign != null)
			font.draw(batch, text, x, y, boxWidth, textAlign, false);
		else
			font.draw(batch, text, x, y);
        if(!skipBatch) batch.end();
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
