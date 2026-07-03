package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Supplier;

/**
 * 화면 내의 컨트롤
 *
 * @constructor 동적 위치를 사용하는 코틀린 전용 생성자
 * @param x      X 좌표 계산 함수. screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 * @param y      Y 좌표 계산 함수
 * @param width  컨트롤 너비 계산 함수
 * @param height 컨트롤 높이 계산 함수
 */
abstract class Widget(x: () -> Float, y: () -> Float, width: () -> Float, height: () -> Float) {
	// 자바에서 좌표나 크기 계산 함수를 구하려면 get*Supplier 메쏘드를 사용할 것.
	@get:JvmSynthetic protected var calculateX: () -> Float = x
		private set;
	@get:JvmSynthetic protected var calculateY: () -> Float = y
		private set;
	@get:JvmSynthetic protected var calculateWidth: () -> Float = width
		private set;
	@get:JvmSynthetic protected var calculateHeight: () -> Float = height
		private set;
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
	 * 화면 내의 컨트롤 (자바 전용 생성자)
	 *
	 * @constructor 자바 개발자 전용 생성자
	 * @param x      X 좌표
	 * @param y      Y 좌표
	 * @param width  컨트롤 너비
	 * @param height 컨트롤 높이
	 */
	constructor(x: Supplier<Float>, y: Supplier<Float>, width: Supplier<Float>, height: Supplier<Float>) : this(x::get, y::get, width::get, height::get);

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
	 * 자바 개발자용 - X 좌표 계산 함수를 받는다. 코틀린에서는 그냥 .calculateX 하면 된다.
	 *
	 * @return 계산 함수
	 */
	protected fun getXSupplier(): Supplier<Float> = Supplier(calculateX::invoke);

	/**
	 * 자바 개발자용 - Y 좌표 계산 함수를 받는다. 코틀린에서는 그냥 .calculateY 하면 된다.
	 *
	 * @return 계산 함수
	 */
	protected fun getYSupplier(): Supplier<Float> = Supplier(calculateY::invoke);

	/**
	 * 자바 개발자용 - 너비 계산 함수를 받는다. 코틀린에서는 그냥 .calculateWidth 하면 된다.
	 *
	 * @return 계산 함수
	 */
	protected fun getWidthSupplier(): Supplier<Float> = Supplier(calculateWidth::invoke);

	/**
	 * 자바 개발자용 - 높이 계산 함수를 받는다. 코틀린에서는 그냥 .calculateHeight 하면 된다.
	 *
	 * @return 계산 함수
	 */
	protected fun getHeightSupplier(): Supplier<Float> = Supplier(calculateHeight::invoke);

	/**
	 * 컨트롤의 현재 계산된 X 좌표
	 * 
	 * @return X 좌표
	 */
	fun getX(): Float = calculateX();

	/**
	 * 컨트롤의 현재 계산된 Y 좌표
	 * 
	 * @return Y 좌표
	 */
	fun getY(): Float = calculateY();

	/**
	 * 컨트롤의 현재 계산된 너비
	 * 
	 * @return 너비
	 */
	fun getWidth(): Float = calculateWidth();

	/**
	 * 컨트롤의 현재 계산된 높이
	 * 
	 * @return 높이
	 */
	fun getHeight(): Float = calculateHeight();

	/**
	 * 컨트롤의 X 좌표를 지정한다.
	 *
	 * @param x 새 X 좌표
	 */
	fun setX(x: Float) {
		calculateX = { x };
	}

	/**
	 * 컨트롤의 X 좌표 식을 지정한다. (코틀린 전용)
	 *
	 * @param supplier 계산 식
	 */
	@JvmSynthetic fun setX(supplier: () -> Float) {
		calculateX = supplier;
	}

	/**
	 * 컨트롤의 X 좌표 식을 지정한다 (자바 전용).
	 *
	 * @param supplier 계산 식
	 */
	fun setX(supplier: Supplier<Float>) {
		calculateX = supplier::get;
	}

	/**
	 * 컨트롤의 Y 좌표를 지정한다.
	 *
	 * @param y 새 Y 좌표
	 */
	fun setY(y: Float) {
		calculateY = { y };
	}

	/**
	 * 컨트롤의 Y 좌표 식을 지정한다. (코틀린 전용)
	 *
	 * @param supplier 계산 식
	 */
	@JvmSynthetic fun setY(supplier: () -> Float) {
		calculateY = supplier;
	}

	/**
	 * 컨트롤의 Y 좌표 식을 지정한다 (자바 전용).
	 *
	 * @param supplier 계산 식
	 */
	fun setY(supplier: Supplier<Float>) {
		calculateY = supplier::get;
	}

	/**
	 * 컨트롤의 너비를 지정한다.
	 *
	 * @param width 새 너비
	 */
	fun setWidth(width: Float) {
		calculateWidth = { width };
	}

	/**
	 * 컨트롤의 너비 식을 지정한다. (코틀린 전용)
	 *
	 * @param supplier 계산 식
	 */
	@JvmSynthetic fun setWidth(supplier: () -> Float) {
		calculateWidth = supplier;
	}

	/**
	 * 컨트롤의 너비 식을 지정한다 (자바 전용).
	 *
	 * @param supplier 계산 식
	 */
	fun setWidth(supplier: Supplier<Float>) {
		calculateWidth = supplier::get;
	}

	/**
	 * 컨트롤의 높이를 지정한다.
	 *
	 * @param height 새 높이
	 */
	fun setHeight(height: Float) {
		calculateHeight = { height };
	}

	/**
	 * 컨트롤의 높이 식을 지정한다. (코틀린 전용)
	 *
	 * @param supplier 계산 식
	 */
	@JvmSynthetic fun setHeight(supplier: () -> Float) {
		calculateHeight = supplier;
	}

	/**
	 * 컨트롤의 높이 식을 지정한다 (자바 전용).
	 *
	 * @param supplier 계산 식
	 */
	fun setHeight(supplier: Supplier<Float>) {
		calculateHeight = supplier::get;
	}
}
