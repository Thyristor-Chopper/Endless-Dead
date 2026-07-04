package io.potatogun.gdxhelper.util;

import org.jetbrains.annotations.NotNull;

/**
 * '원시' float 반환형을 사용하는 람다함수
 */
@FunctionalInterface
public interface FloatSupplier {
	/**
	 * 람다함수를 호출하여 반환값을 가져온다.
	 *
	 * @return 함수의 반환값
	 */
	float getAsFloat();
}
