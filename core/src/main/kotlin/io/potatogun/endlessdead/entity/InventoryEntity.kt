package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Item;

/**
 * 인벤토리를 가진 개체에 대한 인터페이스
 */
interface InventoryEntity {
	/**
	 * 선택한 아이템
	 */
	val selectedItem: Item?;
	/**
	 * 선택한 아이템의 인덱스
	 */
	val selectedItemIndex: Int?;  // 이건 nullable이라 아마 Integer로 컴파일될 것 같은데
	/**
	 * 인벤토리 내 아이템 개수
	 */
	val itemCount: Int;
	/**
	 * 인벤토리가 비어 있는지의 여부
	 */
	val isInventoryEmpty: Boolean;
	/**
	 * 인벤토리 아이템 최대 개수
	 */
	val maxSlots: Int;
	/**
	 * 처음으로 추가된 아이템
	 */
	val firstItem: Item?;
	/**
	 * 마지막으로 추가된 아이템
	 */
	val lastItem: Item?;

	// @JvmOverloads이 불가능해서 수동으로
	fun addItem(item: Item): Boolean = addItem(item, false);

	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item 추가할 아이템
	 * @return     성공 여부 (이미 있으면 실패)
	 */
	fun addItem(item: Item, select: Boolean): Boolean;

	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param index 아이템 위치
	 * @throws IllegalArgumentException 인덱스가 범위를 벗어난 경우
	 */
	fun removeItem(index: Int);

	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param item 제거할 아이템
	 * @return     성공 여부
	 */
	fun removeItem(item: Item): Boolean;

	/**
	 * 지정한 아이템 가져오기
	 *
	 * @param index 아이템 인벤토리 인덱스
	 * @return      해당하는 아이템
	 * @throws IllegalArgumentException 인덱스가 범위를 벗어난 경우
	 */
	fun getItem(index: Int): Item;

	/**
	 * 인벤토리의 다음 아이템 선택
	 *
	 * @return 성공 여부
	 */
	fun selectNextItem(): Boolean;

	/**
	 * 인벤토리의 이전 아이템 선택
	 *
	 * @return 성공 여부
	 */
	fun selectPreviousItem(): Boolean;

	/**
	 * 지정한 아이템을 갖고 있다면 선택한다.
	 *
	 * @param item 선택할 아이템
	 * @return     성공 여부
	 */
	fun selectItem(item: Item): Boolean;

	/**
	 * 지정한 인덱스의 아이템을 선택한다.
	 *
	 * @param index 아이템 인벤토리 인덱스
	 * @return      아이템의 인덱스
	 * @throws IllegalArgumentException 인덱스가 범위를 벗어난 경우
	 */
	fun selectItem(index: Int);

	/**
	 * 지정한 아이템이 있는지 확인
	 *
	 * @return 있으면 true
	 */
	fun hasItem(item: Item): Boolean;

	/**
	 * 인벤토리의 읽기용 사본을 가져온다.
	 *
	 * @return 인벤토리 아이템 목록
	 */
	fun getInventory(): List<Item>;

	/**
	 * 아이템이 파괴될 때 호출되는 콜백 함수
	 *
	 * @param item 파괴된 아이템
	 */
	fun onItemDestoryed(item: Item) {}

	/**
	 * 인벤토리 비우기
	 */
	fun clearInventory();
}
