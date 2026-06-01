package io.potatogun.endlessdead.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 화면 내의 컨트롤
 *
 * @param x			X 좌표 계산 함수. screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 * @param y			Y 좌표 계산 함수
 * @param width		컨트롤 너비
 * @param height	컨트롤 높이
 */
abstract class Widget(var x: () -> Float, var y: () -> Float, @JvmField val width: Float, @JvmField val height: Float) {
	/**
	 * 컨트롤이 화면에 그려지는지의 여부
	 */
	var isVisible = true
		private set;

	/**
	 * 컨트롤을 화면에 그리는 로직
	 */
	internal abstract fun draw(batch: SpriteBatch);

	/**
	 * 자원 해제
	 */
	open fun dispose() {}

	/**
	 * 컨트롤을 보인다.
	 */
	fun show() {
		isVisible = true;
	}

	/**
	 * 컨트롤을 숨긴다.
	 */
	fun hide() {
		isVisible = false;
	}
}
