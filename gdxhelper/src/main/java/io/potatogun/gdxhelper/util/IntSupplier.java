package io.potatogun.gdxhelper.util;

/**
 * 원시 int 반환형을 사용하는 람다함수
 */
@FunctionalInterface
public interface IntSupplier {
	/**
	 * 람다함수를 호출하여 반환값을 가져온다.
	 *
	 * @return 함수의 반환값
	 */
	int getAsInt();

	/**
	 * 지정한 정적 값을 반환하는 함수를 생성한다.
	 *
	 * @param 반환 값
	 * @return 람다 함수
	 */
	static IntSupplier of(int value) {
		return () -> value;
	}
}
