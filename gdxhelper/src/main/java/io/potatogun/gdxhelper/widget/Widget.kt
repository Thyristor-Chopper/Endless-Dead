package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.gdxhelper.function.FloatSupplier;

/**
 * 화면 내의 컨트롤
 *
 * @constructor 동적 위치를 사용하는 생성자
 * @param x      X 좌표 계산 함수. screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 * @param y      Y 좌표 계산 함수
 * @param width  컨트롤 너비 계산 함수
 * @param height 컨트롤 높이 계산 함수
 */
abstract class Widget(x: FloatSupplier, y: FloatSupplier, width: FloatSupplier, height: FloatSupplier) {
	private var xSupplier: FloatSupplier = x;
	private var ySupplier: FloatSupplier = y;
	private var widthSupplier: FloatSupplier = width;
	private var heightSupplier: FloatSupplier = height;
	/**
	 * 컨트롤이 화면에 그려지는지의 여부
	 */
	var isVisible = true
		private set;

	/**
	 * 화면 내의 컨트롤
	 *
	 * @constructor 정적 위치를 사용하는 생성자
	 * @param x      X 좌표
	 * @param y      Y 좌표
	 * @param width  컨트롤 너비
	 * @param height 컨트롤 높이
	 */
	constructor(x: Float, y: Float, width: Float, height: Float) : this({ x }, { y }, { width }, { height });

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
	fun getX(): Float = xSupplier.getAsFloat();

	/**
	 * 컨트롤의 현재 계산된 Y 좌표
	 * 
	 * @return Y 좌표
	 */
	fun getY(): Float = ySupplier.getAsFloat();

	/**
	 * 컨트롤의 현재 계산된 너비
	 * 
	 * @return 너비
	 */
	fun getWidth(): Float = widthSupplier.getAsFloat();

	/**
	 * 컨트롤의 현재 계산된 높이
	 * 
	 * @return 높이
	 */
	fun getHeight(): Float = heightSupplier.getAsFloat();

	/**
	 * 컨트롤의 X 좌표를 지정한다.
	 *
	 * @param x 새 X 좌표
	 */
	fun setX(x: Float) {
		xSupplier = { x };
	}

	/**
	 * 컨트롤의 X 좌표 식을 지정한다.
	 *
	 * @param supplier 계산 식
	 */
	fun setX(supplier: FloatSupplier) {
		xSupplier = supplier;
	}

	/**
	 * 컨트롤의 Y 좌표를 지정한다.
	 *
	 * @param y 새 Y 좌표
	 */
	fun setY(y: Float) {
		ySupplier = { y };
	}

	/**
	 * 컨트롤의 Y 좌표 식을 지정한다.
	 *
	 * @param supplier 계산 식
	 */
	fun setY(supplier: FloatSupplier) {
		ySupplier = supplier;
	}

	/**
	 * 컨트롤의 너비를 지정한다.
	 *
	 * @param width 새 너비
	 */
	fun setWidth(width: Float) {
		widthSupplier = { width };
	}

	/**
	 * 컨트롤의 너비 식을 지정한다.
	 *
	 * @param supplier 계산 식
	 */
	fun setWidth(supplier: FloatSupplier) {
		widthSupplier = supplier;
	}

	/**
	 * 컨트롤의 높이를 지정한다.
	 *
	 * @param height 새 높이
	 */
	fun setHeight(height: Float) {
		heightSupplier = { height };
	}

	/**
	 * 컨트롤의 높이 식을 지정한다.
	 *
	 * @param supplier 계산 식
	 */
	fun setHeight(supplier: FloatSupplier) {
		heightSupplier = supplier;
	}
}
