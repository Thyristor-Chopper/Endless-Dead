package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;

import java.util.function.Predicate;

class ArrayView<T>(private val array: GdxArray<T>) : View<T> {
	// 왜인지는 모르겠지만 여기서는 @get:JvmName("size")를 안 넣어도 getSize()가 중복으로 안 생김
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

	override fun filter(condition: Predicate<T>): GdxArray<T> {
		val output = GdxArray<T>(array.size);
		for(i in 0 until array.size) {
			val element = array[i];
			if(condition.test(element))
				output.add(element);
		}
		return output;
	}

	override fun filter(condition: Predicate<T>, output: GdxArray<T>) {
		output.clear();
		for(i in 0 until array.size) {
			val element = array[i];
			if(condition.test(element))
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

	override fun iterator(): Iterator<T> = GdxArray.ArrayIterator<T>(array, false);
}
