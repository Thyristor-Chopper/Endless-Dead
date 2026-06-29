package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 화면 내의 컨트롤
 *
 * @property x      X 좌표 계산 함수. screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 * @property y      Y 좌표 계산 함수
 * @property width  컨트롤 너비 계산 함수
 * @property height 컨트롤 높이 계산 함수
 */
abstract class Widget(x: () -> Float, y: () -> Float, width: () -> Float, height: () -> Float) {
	@get:JvmName("getLeftCalculator") protected var x: () -> Float = x
		private set;
	@get:JvmName("getBottomCalculator") protected var y: () -> Float = y
		private set;
	@get:JvmName("getWidthCalculator") protected var width: () -> Float = width
		private set;
	@get:JvmName("getHeightCalculator") protected var height: () -> Float = height
		private set;
	/**
	 * 컨트롤이 화면에 그려지는지의 여부
	 */
	var isVisible = true
		private set;

	/**
	 * 컨트롤을 화면에 그리는 로직
	 *
	 * @param batch 이미지(Texture)를 화면에 찍어주는 도구
	 */
	abstract fun draw(batch: SpriteBatch);

	/**
	 * 자원을 해제한다.
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

	/**
	 * 컨트롤의 현재 계산된 X 좌표
	 * 
	 * @return X 좌표
	 */
	fun getX(): Float = x();

	/**
	 * 컨트롤의 현재 계산된 Y 좌표
	 * 
	 * @return Y 좌표
	 */
	fun getY(): Float = y();

	/**
	 * 컨트롤의 현재 계산된 너비
	 * 
	 * @return 너비
	 */
	fun getWidth(): Float = width();

	/**
	 * 컨트롤의 현재 계산된 높이
	 * 
	 * @return 높이
	 */
	fun getHeight(): Float = height();

	/**
	 * 컨트롤의 X 좌표 식을 지정한다.
	 */
	fun setX(fn: () -> Float) {
		x = fn;
	}

	/**
	 * 컨트롤의 Y 좌표 식을 지정한다.
	 */
	fun setY(fn: () -> Float) {
		y = fn;
	}

	/**
	 * 컨트롤의 너비 식을 지정한다.
	 */
	fun setWidth(fn: () -> Float) {
		width = fn;
	}

	/**
	 * 컨트롤의 높이 식을 지정한다.
	 */
	fun setHeight(fn: () -> Float) {
		height = fn;
	}
}
