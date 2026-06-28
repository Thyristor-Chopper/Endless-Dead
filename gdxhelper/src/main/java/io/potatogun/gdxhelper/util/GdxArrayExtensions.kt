package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;

import kotlin.random.Random;

fun <T> GdxArray<T>.randomOrNull(): T? {
	if(isEmpty()) return null;
	return this[Random.nextInt(size)];
}
