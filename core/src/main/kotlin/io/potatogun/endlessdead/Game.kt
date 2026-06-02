package io.potatogun.endlessdead;

import com.badlogic.gdx.Game as GdxGame;
import com.badlogic.gdx.Gdx;

/**
 * libGDX의 Game 클래스를 확장하여 유용한 기능을 추가한 것.
 */
abstract class Game : GdxGame() {
	// 게임 제목
	abstract val title: String;
	private var titleBarInfo = "";
	private var titleBarStats = "";
    /** 
	 * 화면(창) 크기
	 */
    val screenWidth: Float
		inline get() = Gdx.graphics.width.toFloat();
    val screenHeight: Float
		inline get() = Gdx.graphics.height.toFloat();

	/**
	 * 매 프레임 실행된다.
	 * update를 호출하여 매 프레임 게임 자체의 갱신 로직을 실행한다.
	 */
	override fun render() {
		super.render();
		update();
	}

	/**
	 * 매 프레임 게임 자체의 갱신 로직.
	 */
	protected open fun update() {}
}
