package io.potatogun.gdxhelper.util;

/**
 * 원시 float 반환형을 사용하는 람다함수
 */
@FunctionalInterface
public interface FloatSupplier {
	/**
	 * 람다함수를 호출하여 반환값을 가져온다.
	 *
	 * @return 함수의 반환값
	 */
	float getAsFloat();

	/**
	 * 지정한 정적 값을 반환하는 함수를 생성한다.
	 *
	 * @param 반환 값
	 * @return 람다 함수
	 */
	static FloatSupplier of(float value) {
		return () -> value;
	}
}
