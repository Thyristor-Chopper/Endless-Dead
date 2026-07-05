package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 인벤토리 인터페이스
 */
interface Inventory {
	/**
	 * 인벤토리 내 아이템 개수
	 */
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("size")
	val size: Int;
	/**
	 * 인벤토리가 비어 있는지의 여부
	 */
	val isEmpty: Boolean;
	/**
	 * 인벤토리 아이템 최대 개수
	 */
	val maxSlots: Int;

	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item 추가할 아이템
	 * @return 성공 여부 (이미 있으면 실패)
	 */
	fun addItem(item: Item): Boolean;

	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param index 아이템 위치
	 * @return 성공 여부
	 */
	fun removeItem(index: Int): Boolean;

	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param item 제거할 아이템
	 * @return 성공 여부
	 */
	fun removeItem(item: Item): Boolean;

	/**
	 * 지정한 아이템 가져오기
	 *
	 * @param index 아이템 인벤토리 인덱스
	 * @return 해당하는 아이템
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우
	 */
	fun getItem(index: Int): Item;

	/**
	 * 지정한 아이템이 있는지 확인
	 *
	 * @return 있으면 true
	 */
	fun hasItem(item: Item): Boolean;

	/**
	 * 지정한 아이템의 인덱스
	 *
	 * @return 인덱스 (없으면 -1)
	 */
	fun indexOf(item: Item): Int;

	/**
	 * 인벤토리의 아이템 목록(읽기 전용)을 가져온다.
	 *
	 * @return 인벤토리 아이템 목록
	 */
	fun getItems(): GdxArray<Item>;

	/**
	 * 인벤토리의 아이템 목록을 가져온다.
	 *
	 * @param output 인벤토리 아이템을 저장할 목록 (기존 원소는 덮어씌워짐)
	 */
	fun getItems(output: GdxArray<Item>);

	/**
	 * 모든 아이템을 순회한다.
	 *
	 * @param callback 실행할 서브루틴
	 */
	fun forEachItems(callback: Consumer<Item>);

	/**
	 * 모든 아이템을 역순으로 순회한다.
	 *
	 * @param callback 실행할 서브루틴
	 */
	fun forEachItemsReverse(callback: Consumer<Item>);

	/**
	 * 인벤토리 비우기
	 *
	 * @return 성공 여부
	 */
	fun clear();
}
