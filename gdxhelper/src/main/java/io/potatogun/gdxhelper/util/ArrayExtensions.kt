@file:JvmName("ArrayUtils")
package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;

import kotlin.random.Random;

inline fun <T> GdxArray<T>.randomOrNull(): T? = if(isEmpty()) null else this[Random.nextInt(size)];

inline fun <T> GdxArray<T>.createView(): ArrayView<T> = ArrayView<T>(this);

@JvmName("iteratorOf") inline fun <T> GdxArray<T>.iterator(): ArrayIterator<T> = ArrayIterator<T>(this);
