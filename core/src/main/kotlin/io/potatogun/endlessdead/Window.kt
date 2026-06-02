package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

/**
 * 게임 화면(창)에 대한 것들
 */
object Window {
	val width: Float
		inline get() = Gdx.graphics.width.toFloat();
	val height: Float
		inline get() = Gdx.graphics.height.toFloat();

	inline fun setTitle(title: String) {
		Gdx.graphics.setTitle(title);
	}
}
