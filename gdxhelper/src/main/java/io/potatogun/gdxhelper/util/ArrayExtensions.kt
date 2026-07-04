@file:JvmName("ArrayUtils")
package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;

import kotlin.random.Random;

inline fun <T> GdxArray<T>.randomOrNull(): T? = if(isEmpty()) null else this[Random.nextInt(size)];

/**
 * 배열에 대한 읽기 전용 뷰를 생성한다.
 *
 * 자바에서는 ArrayUtils.createView(array)로 하면 된다.
 *
 * @return 배열 뷰
 */
inline fun <T> GdxArray<T>.createView(): ArrayView<T> = ArrayView<T>(this);
