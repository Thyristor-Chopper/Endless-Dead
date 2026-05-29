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

abstract class Screen(val game: ZombieGame) : ScreenAdapter(), Updatable {
	// OrthographicCamera: 원근 없이(평행 투영) 2D 좌표를 그대로 그려주는 카메라.
    protected val camera = OrthographicCamera();
	// SpriteBatch: 이미지(Texture) 와 글자를 화면에 찍어주는 도구.
    //   배경 그리기·게임 객체·텍스트 모두 이 batch 하나로 처리한다.
    protected val batch = SpriteBatch();
    protected val font = BitmapFont();

    init {
        setCameraCenter();
    }
	
	private inline fun setCameraCenter() {
        // 카메라를 '왼쪽 아래 = (0,0), 오른쪽 위 = (screenWidth, screenHeight)' 로 설정.
        //   false 인자는 y 축을 위로(수학 좌표계처럼) 둔다는 뜻.
		camera.setToOrtho(false, game.screenWidth.toFloat(), game.screenHeight.toFloat());
	}
	
	/**
	 * 크기 조절 시 호출된다.
	 */
	override fun resize(width: Int, height: Int) {
		game.screenWidth = width;
		game.screenHeight = height;
		setCameraCenter();
	}
	
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
	}
	
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
	
	override fun dispose() {
		batch.dispose();
		font.dispose();
	}
}
