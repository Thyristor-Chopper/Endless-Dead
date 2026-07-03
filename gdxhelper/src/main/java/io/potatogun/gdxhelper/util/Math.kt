package io.potatogun.gdxhelper.util;

/**
 * 인라인으로 실행되는 수학 관련 함수들 (자바에서는 무의미)
 */
object Math {
	/**
	 * 두 수 중 최댓값을 반환한다.
	 *
	 * @param x 첫째 수
	 * @param y 둘째 수
	 * @return  최댓값
	 */
	@JvmStatic inline fun max2(x: Float, y: Float): Float = if(x > y) x else y;

	/**
	 * 지정한 수의 절댓값을 반환한다.
	 *
	 * @param n 처리할 수
	 * @return  절댓값
	 */
	@JvmStatic inline fun abs(n: Float): Float = if(n < 0) -n else n;
}