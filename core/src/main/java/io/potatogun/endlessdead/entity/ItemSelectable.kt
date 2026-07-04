package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Item;

/**
 * 아이템을 선택할 수 있는 개체
 */
interface ItemSelectable {
	/**
	 * 선택한 아이템
	 */
	val selectedItem: Item?;
	/**
	 * 선택한 아이템의 인덱스 (선택하지 않았다면 -1)
	 */
	val selectedItemIndex: Int;

	/**
	 * 인벤토리의 다음 아이템 선택
	 *
	 * @return 실제 아이템이 선택됐는지의 여부
	 */
	fun selectNextItem(): Boolean;

	/**
	 * 인벤토리의 이전 아이템 선택
	 *
	 * @return 실제 아이템이 선택됐는지의 여부
	 */
	fun selectPreviousItem(): Boolean;

	/**
	 * 지정한 아이템을 갖고 있다면 선택한다.
	 *
	 * @param item 선택할 아이템
	 * @return 성공 여부
	 */
	fun selectItem(item: Item): Boolean;

	/**
	 * 지정한 인덱스의 아이템을 선택한다.
	 *
	 * @param index 아이템 인벤토리 인덱스
	 * @return 성공 여부
	 */
	fun selectItem(index: Int): Boolean;

	/**
	 * 선택을 해제한다.
	 */
	fun deselect();
}
