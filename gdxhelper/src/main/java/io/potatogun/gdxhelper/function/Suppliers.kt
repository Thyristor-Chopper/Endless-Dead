@file:JvmName("Suppliers")
package io.potatogun.gdxhelper.function;

import java.util.function.IntSupplier;

/**
 * 지정한 정적 정수를 반환하는 함수를 생성한다.
 *
 * @param value 값
 * @return      람다 함수
 */
fun intSupplierOf(value: Int) = IntSupplier { value };

/**
 * 지정한 정적 실수를 반환하는 함수를 생성한다.
 *
 * @param value 값
 * @return      람다 함수
 */
fun floatSupplierOf(value: Float) = FloatSupplier { value };
