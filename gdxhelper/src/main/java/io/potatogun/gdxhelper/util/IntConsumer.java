package io.potatogun.gdxhelper.util;

/**
 * '원시' int 매개변수를 사용하는 람다함수
 */
@FunctionalInterface
public interface IntConsumer {
	/**
	 * 람다함수를 호출한다.
	 *
	 * @param x 매개변수
	 */
	void accept(int x);
}
