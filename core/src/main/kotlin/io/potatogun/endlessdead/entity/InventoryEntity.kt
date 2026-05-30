package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Item;

/**
 * 인벤토리를 가진 개체에 대한 인터페이스
 */
interface InventoryEntity {
	val selectedItem: Item?;
	val selectedItemIndex: Int?;
	val inventoryItemCount: Int;
	val isInventoryEmpty: Boolean;

	/**
	 * 인벤토리에 아이템 넣기
	 *
	 * @param item	추가할 아이템
	 * @return 	성공 여부 (이미 있으면 실패)
	 */
	fun addItemToInventory(item: Item, select: Boolean = false): Boolean;

	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param	index	아이템 위치
	 * @return 	성공 여부
	 */
	fun removeItemFromInventory(index: Int);

	/**
	 * 인벤토리에서 아이템 빼기
	 *
	 * @param 	item	제거할 아이템
	 * @return 	성공 여부
	 */
	fun removeItemFromInventory(item: Item): Boolean;

	/**
	 * 인벤토리의 다음 아이템 선택
	 *
	 * @return 	성공 여부
	 */
	fun selectNextItem(): Boolean;

	/**
	 * 인벤토리의 이전 아이템 선택
	 *
	 * @return 	성공 여부
	 */
	fun selectPreviousItem(): Boolean;

	/**
	 * 지정한 아이템을 갖고 있다면 선택한다.
	 *
	 * @return 성공 여부
	 */
	fun selectItem(item: Item): Boolean;

	/**
	 * 지정한 인덱스의 아이템을 선택한다.
	 */
	fun selectItem(index: Int);

	/**
	 * 지정한 아이템이 있는지 확인
	 */
	fun hasItem(item: Item): Boolean;

	/**
	 * 인벤토리의 읽기용 사본을 가져온다.
	 */
	fun getInventory(): List<Item>;

	/**
	 * 아이템이 파괴될 때 호출되는 콜백 함수
	 */
	fun onItemDestoryed(item: Item) {}
}
