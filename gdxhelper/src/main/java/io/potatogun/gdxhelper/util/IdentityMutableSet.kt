package io.potatogun.gdxhelper.util;

import java.util.IdentityHashMap;

/**
 * 메모리 주소 참조를 갖는 집합
 */
class IdentityMutableSet<T> : MapKeyMutableSet<T>() {
	override val map = IdentityHashMap<T, Unit>();
}
