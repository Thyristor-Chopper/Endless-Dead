package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;

class ArrayIterator<T>(private val array: GdxArray<T>) : Iterator<T> {
	private var index = -1;

	override fun hasNext(): Boolean = index < array.size - 1;

	override fun next(): T {
		if(index == array.size - 1)
			throw NoSuchElementException("no more entities to iterate");
		return array[++index];
	}
}
