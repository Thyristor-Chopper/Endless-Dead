package com.oop.game;

import com.oop.game.item.Item;

/**
 * 아이템을 가질 수 있는 개체를 모두 핸들링하기 위해 필요 (다형성)
 */
interface ItemHolder {
	/**
	 * 현재 들고 있는 아이템을 가져온다.
	 *
	 * @return 들고 있는 아이템 (없으면 null)
	 */
	fun getHoldingItem(): Item?;
	
	/**
	 * 현재 들고 있는 아이템을 지정한 것으로 바꾸거나 전환한다.
	 */
	fun setHoldingItem(item: Item);
	
	/**
	 * 현재 들고 있는 아이템을 파괴한다.
	 *
	 * @return 성공 여부
	 */
	fun destroyHoldingItem(): Boolean;
	
	/**
	 * 지정한 아이템을 들고 있으면 파괴한다.
	 *
	 * @return 성공 여부
	 */
	fun destroyItem(item: Item): Boolean;
}
