package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;

class ArrayView<T>(private val array: GdxArray<T>) : View<T> {
	override val size: Int
		get() = array.size;
	override val isEmpty: Boolean
		get() = array.isEmpty();

	override operator fun get(index: Int): T = array[index];

	override fun sortedWith(comparator: Comparator<T>): GdxArray<T> {
		val output = clone();
		Utils.sortWith<T>(output, comparator);
		return output;
	}

	override fun sortedWith(comparator: Comparator<T>, output: GdxArray<T>) {
		clone(output);
		Utils.sortWith<T>(output, comparator);
	}

	override fun filter(condition: (T) -> Boolean): GdxArray<T> {
		val output = GdxArray<T>(array.size);
		for(i in 0 until array.size) {
			val element = array[i];
			if(condition(element))
				output.add(element);
		}
		return output;
	}

	override fun filter(condition: (T) -> Boolean, output: GdxArray<T>) {
		output.clear();
		for(i in 0 until array.size) {
			val element = array[i];
			if(condition(element))
				output.add(element);
		}
	}

	override fun clone(): GdxArray<T> {
		val output = GdxArray<T>(array.size);
		for(i in 0 until array.size)
			output.add(array[i]);
		return output;
	}

	override fun clone(output: GdxArray<T>) {
		output.clear();
		for(i in 0 until array.size)
			output.add(array[i]);
	}

	override fun forEach(callback: (T) -> Unit) {
		for(i in 0 until array.size)
			callback(array[i]);
	}

	override fun iterator(): Iterator<T> = GdxArray.ArrayIterator<T>(array, false);
}
