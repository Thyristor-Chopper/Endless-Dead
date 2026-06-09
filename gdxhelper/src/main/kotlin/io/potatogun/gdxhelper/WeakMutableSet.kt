package io.potatogun.gdxhelper;

import java.util.WeakHashMap;

/**
 * 약한 참조를 갖는 집합.
 * 강한 참조가 모두 사라지면 집합에서 자동으로 없어진다.
 *
 * 자바의 WeakHashMap의 키를 활용하여 구현하였다.
 *
 * WeakList나 WeakSet같은 게 왜 없는 거지... 있으면 쓸 데가 많을텐데
 */
class WeakMutableSet<T> : MutableSet<T> {
	private val map = WeakHashMap<T, Unit>();
	private val keys = map.keys;
	/**
	 * 집합의 크기
	 */
	override val size: Int
		get() = map.size;

	override fun isEmpty(): Boolean = map.isEmpty();

	/**
	 * 지정한 요소가 있는지 확인한다.
	 */
	override fun contains(element: T): Boolean = map.containsKey(element);

	/**
	 * 지정한 요소들이 모두 있는지 확인한다.
	 */
	override fun containsAll(elements: Collection<T>): Boolean = keys.containsAll(elements);

	/**
	 * 지정한 요소를 추가한다.
	 *
	 * @return 이미 있으면 false, 그 외에는 true
	 */
	override fun add(element: T): Boolean = (map.put(element, Unit) == null);

	/**
	 * 지정한 요소를 삭제한다.
	 *
	 * @return 있으면 true, 없었으면 false
	 */
	override fun remove(element: T): Boolean = (map.remove(element) != null);

	/**
	 * 지정한 목록의 요소들을 추가한다.
	 *
	 * @return 하나라도 추가된 게 있으면 true
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
	 * @return 하나라도 지워진 게 있으면 true
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
	 * @return 하나라도 지워진 게 있으면 true
	 */
	override fun retainAll(elements: Collection<T>): Boolean = keys.retainAll(elements);

	/**
	 * 집합을 비운다.
	 */
	override fun clear() {
		map.clear();
	}

	/**
	 * 순회기
	 */
	override fun iterator(): MutableIterator<T> = keys.iterator();
}
