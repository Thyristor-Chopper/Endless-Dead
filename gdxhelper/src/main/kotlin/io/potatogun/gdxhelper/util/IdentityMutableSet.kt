package io.potatogun.gdxhelper.util;

import java.util.IdentityHashMap;

/**
 * 메모리 주소 참조를 갖는 집합
 */
class IdentityMutableSet<T> : MapKeyMutableSet<T>() {
	override val map = IdentityHashMap<T, Unit>();
}

/**
 * 지정한 요소들을 담아서 약참조 집합을 만든다.
 *
 * @param items 담을 요소들
 * @return      만들어진 집합
 */
fun <T> identityMutableSetOf(vararg items: T): IdentityMutableSet<T> {
	val mutableSet = IdentityMutableSet<T>();
	mutableSet.addAll(items);
	return mutableSet;
}
