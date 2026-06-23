package io.potatogun.gdxhelper.util;

import java.util.WeakHashMap;

import kotlin.reflect.KClass;

/**
 * 맵의 키를 사용하는 집합
 */
abstract class MapKeyMutableSet<T> : MutableSet<T> {
	protected abstract val map: MutableMap<T, Unit>;
	private val keys by lazy { map.keys };
	/**
	 * 집합의 크기
	 */
	override val size: Int
		get() = map.size;

	/**
	 * 집합이 비어 있는지의 여부
	 *
	 * @return 비어 있으면 true
	 */
	override fun isEmpty(): Boolean = map.isEmpty();

	/**
	 * 지정한 요소가 있는지 확인한다.
	 *
	 * @param element 확인할 요소
	 * @return        있으면 true, 없으면 false
	 */
	override fun contains(element: T): Boolean = map.containsKey(element);

	/**
	 * 지정한 요소들이 모두 있는지 확인한다.
	 *
	 * @param elements 확인할 요소들의 컬렉션
	 * @return         하나라도 빠지면 false
	 */
	override fun containsAll(elements: Collection<T>): Boolean = keys.containsAll(elements);

	/**
	 * 지정한 요소를 추가한다.
	 *
	 * @param element 추가할 요소
	 * @return        이미 있으면 false, 아니면 true
	 */
	override fun add(element: T): Boolean = (map.put(element, Unit) == null);

	/**
	 * 지정한 요소를 삭제한다.
	 *
	 * @param element 제거할 요소
	 * @return        있으면 true, 없었으면 false
	 */
	override fun remove(element: T): Boolean = (map.remove(element) != null);

	/**
	 * 지정한 목록의 요소들을 추가한다.
	 *
	 * @param elements 추가할 요소들의 컬렉션
	 * @return         하나라도 추가된 게 있으면 true
	 */
	override fun addAll(elements: Collection<T>): Boolean {
		var ret = false;
		for(item in elements)
			ret = (map.put(item, Unit) == null) || ret;
		return ret;
	}

	/**
	 * 지정한 목록의 요소들을 삭제한다.
	 *
	 * @param elements 날릴 요소들의 컬렉션
	 * @return         하나라도 지워진 게 있으면 true
	 */
	override fun removeAll(elements: Collection<T>): Boolean {
		var ret = false;
		for(item in elements)
			ret = (map.remove(item) != null) || ret;
		return ret;
	}

	/**
	 * 지정한 목록의 요소만 남기고 싹 다 날린다.
	 *
	 * @param elements 유지할 요소들의 컬렉션
	 * @return         하나라도 지워진 게 있으면 true
	 */
	override fun retainAll(elements: Collection<T>): Boolean = keys.retainAll(elements);

	/**
	 * 집합을 비운다.
	 */
	override fun clear() {
		map.clear();
	}

	/**
	 * 순회기를 반환한다.
	 *
	 * @return 순회기
	 */
	override fun iterator(): MutableIterator<T> = keys.iterator();
}
