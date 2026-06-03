package io.potatogun.gdxhelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input as GdxInput;
import com.badlogic.gdx.InputProcessor;

/**
 * 키보드 입력을 편리하게 읽는 도우미.
 *
 * ────────────────────────────────────────────────────────────
 *  왜 이런 게 필요한가?
 * ────────────────────────────────────────────────────────────
 *  LibGDX에서 키 입력을 확인하려면 매번
 *      Gdx.input.isKeyPressed(Input.Keys.LEFT)
 *  처럼 길게 적어야 한다. 게임 로직 안에서 이 긴 표현이 반복되면
 *  코드가 지저분해지고 핵심 흐름이 잘 보이지 않는다.
 *
 *  그래서 자주 쓰는 입력만 모아 짧은 이름으로 감싸둔다.
 *      Input.isKeyPressed(Input.LEFT)
 *
 * ────────────────────────────────────────────────────────────
 *  object 키워드
 * ────────────────────────────────────────────────────────────
 *  'object'는 '싱글톤 객체'를 만드는 키워드다.
 *    - 인스턴스가 단 하나뿐이고
 *    - 어디서든 Input.XXX 로 바로 접근 가능
 *  입력처럼 '상태는 시스템 하나에만 존재'하는 자원에 잘 어울린다.
 *  (파이썬이라면 모듈 수준 함수/변수와 비슷한 역할)
 */
object Input {
	private var scrolledUp = false;
	private var scrolledDown = false;
	private var keyDown = false;
	private var keyJustDown = false;
	val mouseX: Int
		inline get() = Gdx.input.getX();
	val mouseY: Int
		inline get() = Gdx.input.getY();

	init {
		// https://stackoverflow.com/questions/17644429/libgdx-mouse-just-clicked 참고함
		Gdx.input.setInputProcessor(object : InputProcessor {
			override fun scrolled(amountX: Float, amountY: Float): Boolean {
				if(amountY > 0f) {
					scrolledDown = true;
					Gdx.app.postRunnable { scrolledDown = false };
					return true;
				} else if(amountY < 0f) {
					scrolledUp = true;
					Gdx.app.postRunnable { scrolledUp = false };
					return true;
				}
				return false;
			}

			override fun keyDown(code: Int): Boolean {
				keyJustDown = true;
				keyDown = true;
				Gdx.app.postRunnable { keyJustDown = false };
				return true;
			}

			override fun keyUp(code: Int): Boolean {
				keyDown = false;
				keyJustDown = false;
				return true;
			}

			override fun mouseMoved(x: Int, y: Int): Boolean = false;

			override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean = false;

			override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean = false;

			override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean = false;

			override fun touchCancelled(x: Int, y: Int, pointer: Int, button: Int): Boolean = false;
			
			override fun keyTyped(char: Char): Boolean = false;
		});
	}

    /**
     * 키가 현재 '눌려 있는 중' 인지 — 꾹 누르고 있으면 매 프레임 true.
     *   이동(← → ↑ ↓) 처럼 '누르는 동안 계속' 일어나야 할 동작에 사용.
     */
    inline fun isKeyPressed(key: Int): Boolean = Gdx.input.isKeyPressed(key);

    /**
     * 키가 '이번 프레임에 막 눌렸는지' — 꾹 눌러도 첫 프레임에만 true.
     *   총알 발사, 메뉴 선택처럼 '한 번만' 실행되어야 할 동작에 사용.
     */
    inline fun isKeyJustPressed(key: Int): Boolean = Gdx.input.isKeyJustPressed(key);

    /**
     * 아무 키라도 눌려 있는지의 여부
     */
	fun isAnyKeyPressed(): Boolean = keyDown;

    /**
     * 아무 키를 방금 눌렀는지의 여부
     */
	fun isAnyKeyJustPressed(): Boolean = keyJustDown;

	/**
     * 지정한 마우스 단추가 눌려 있는지의 여부
	 *
	 * @param button	단추의 종류
     */
    inline fun isButtonPressed(button: Int): Boolean = Gdx.input.isButtonPressed(button);

	/**
     * 지정한 마우스 단추를 막 눌렀는지의 여부
	 *
	 * @param button	단추의 종류
     */
    inline fun isButtonJustPressed(button: Int): Boolean = Gdx.input.isButtonJustPressed(button);

	/**
     * 방금 아래로 스크롤됐는지의 여부
     */
    fun isScrolledDown(): Boolean = scrolledDown;

	/**
     * 방금 위로 스크롤됐는지의 여부
     */
    fun isScrolledUp(): Boolean = scrolledUp;

    // 자주 쓰는 키 상수를 짧은 이름으로 재노출.
    //   원본은 Input.Keys.LEFT 처럼 길어서 자주 쓸수록 번거롭다.
    //   필요하면 Input.Keys.XXX 에서 다른 키를 직접 import 해서 써도 된다.
    const val LEFT = GdxInput.Keys.LEFT;
    const val RIGHT = GdxInput.Keys.RIGHT;
    const val UP = GdxInput.Keys.UP;
    const val DOWN = GdxInput.Keys.DOWN;
    const val SPACE = GdxInput.Keys.SPACE;
    const val ESCAPE = GdxInput.Keys.ESCAPE;
    const val W = GdxInput.Keys.W;
    const val A = GdxInput.Keys.A;
    const val S = GdxInput.Keys.S;
    const val D = GdxInput.Keys.D;
    const val P = GdxInput.Keys.P;
    const val R = GdxInput.Keys.R;
    const val DELETE = GdxInput.Keys.FORWARD_DEL;
    const val LEFT_MOUSE = GdxInput.Buttons.LEFT;
    const val RIGHT_MOUSE = GdxInput.Buttons.RIGHT;
}
