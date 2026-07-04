package io.potatogun.gdxhelper.function;

/**
 * '원시' float 매개변수 두 개를 사용하는 람다함수
 */
@FunctionalInterface
public interface FloatBiConsumer {
	/**
	 * 람다함수를 호출한다.
	 *
	 * @param x 첫째 매개변수
	 * @param y 둘째 매개변수
	 */
	void accept(float x, float y);
}
