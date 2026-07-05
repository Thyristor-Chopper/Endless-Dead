package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.ItemSelectable;

/**
 * '사용할 수 있다'의 개념이 있는 아이템
 */
interface Usable {
	/**
	 * 마우스를 꾹 누를 때 연속으로 발동 가능한지의 여부
	 */
	val isContinuousUseAllowed: Boolean;

	/**
	 * 아이템을 사용한다.
	 *
	 * @param user 아이템 사용자 (이 아이템을 실제로 들고 있어야 함)
	 * @return 사용 성공 여부 (사용자가 이 아이템을 들고 있지 않은 경우 실패)
	 */
	fun use(user: ItemSelectable): Boolean;
}
