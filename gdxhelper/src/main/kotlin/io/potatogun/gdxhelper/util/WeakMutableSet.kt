package io.potatogun.gdxhelper.util;

import java.util.WeakHashMap;

/**
 * 약한 참조를 갖는 집합.
 * 강한 참조가 모두 사라지면 집합에서 자동으로 없어진다.
 *
 * 자바의 WeakHashMap의 키를 활용하여 구현하였다.
 *
 * WeakList나 WeakSet같은 게 왜 없는 거지... 있으면 쓸 데가 많을텐데
 */
class WeakMutableSet<T> : MapKeyMutableSet<T>() {
	override val map = WeakHashMap<T, Unit>();
}

/**
 * 지정한 요소들을 담아서 약참조 집합을 만든다.
 *
 * @param items 담을 요소들
 * @return      만들어진 집합
 */
fun <T> weakMutableSetOf(vararg items: T): WeakMutableSet<T> {
	val mutableSet = WeakMutableSet<T>();
	mutableSet.addAll(items);
	return mutableSet;
}
