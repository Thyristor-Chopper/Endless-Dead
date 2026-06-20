package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 화면 내의 컨트롤
 *
 * @JvmField가 있는 곳은 빌드 후 직접 디컴파일하여 null이 불가능한 원시 int, float로 바뀜을 확인했다.
 *
 * @property x      X 좌표 계산 함수. screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 * @property y      Y 좌표 계산 함수
 * @property width  컨트롤 너비
 * @property height 컨트롤 높이
 */
abstract class Widget(protected var x: () -> Float, protected var y: () -> Float, @JvmField val width: Float, @JvmField val height: Float) {
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
	internal abstract fun draw(batch: SpriteBatch);

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
}
