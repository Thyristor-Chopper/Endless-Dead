package io.potatogun.gdxhelper.collections;

import com.badlogic.gdx.utils.Array as GdxArray;

import java.util.function.Predicate;

/**
 * 리스트, 배열, 컬렉션 등을 '읽기 전용'으로 상호작용할 수 있는 뷰이다.
 *
 * 참고: forEach는 코틀린이 자동으로 제공한다.
 */
interface View<T> : Iterable<T> {
	/**
	 * 총 개체 수 - 자바에서는 view.size()
	 */
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("size")
	val size: Int;
	/**
	 * 개체 목록이 비어 있는지의 여부 - 자바에서는 view.isEmpty()
	 */
	val isEmpty: Boolean;

	/**
	 * 지정한 인덱스의 개체를 가져온다. 코틀린에서는 view[N], 자바에서는 view.get(N)
	 *
	 * @param index 인덱스
	 * @return      개체
	 */
	operator fun get(index: Int): T;

	/**
	 * 개체 목록을 지정한 비교기로 정렬한다.
	 *
	 * @param comparator 비교기
	 * @return           정렬된 목록
	 */
	fun sortedWith(comparator: Comparator<T>): GdxArray<T> {
		val output = GdxArray<T>(false, size);
		sortedWith(comparator, output);
		return output;
	}

	/**
	 * 개체 목록을 지정한 비교기로 정렬한다.
	 *
	 * @param comparator 비교기
	 * @param output     정렬 결과를 저장할 목록 (이미 다른 원소가 있다면 덮어씌워짐)
	 */
	fun sortedWith(comparator: Comparator<T>, output: GdxArray<T>);

	/**
	 * 지정한 조건에 해당하는 개제만 모은다.
	 *
	 * @param condition 조건
	 * @return 결과 목록
	 */
	fun filter(condition: Predicate<T>): GdxArray<T> {
		val output = GdxArray<T>(false, size);
		filter(condition, output);
		return output;
	}

	/**
	 * 지정한 조건에 해당하는 개제만 모은다 - 자바 개발자도 편하게 구현할 수 있도록 Predicate를 사용하며 코틀린에서 호출할 때는 일반 { x -> ... } 람다로 여전히 호출 가능하다.
	 *
	 * @param condition 조건
	 * @param output    결과를 저장할 목록 (이미 다른 원소가 있다면 덮어씌워짐)
	 */
	fun filter(condition: Predicate<T>, output: GdxArray<T>);

	/**
	 * 개체 목록을 새 배열로 복사한다.
	 *
	 * @return 복사된 배열
	 */
	fun clone(): GdxArray<T> {
		val output = GdxArray<T>(false, size);
		clone(output);
		return output;
	}

	/**
	 * 개체 목록을 지정한 배열로 복사한다.
	 *
	 * @param output 대상 배열 (기존 원소는 덮어씌워짐)
	 */
	fun clone(output: GdxArray<T>);

	/**
	 * 순회기를 반환한다.
	 *
	 * @return 순회기
	 */
	override fun iterator(): Iterator<T>;
}
