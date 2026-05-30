package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.Position;
import io.potatogun.endlessdead.entity.Entity;

/**
 * '쏘기'가 가능한 아이템
 */
interface Fireable {
	/**
	 * 마우스를 꾹 누를 때 연속으로 쏠 수 있는지의 여부
	 */
	val allowContinuousFire: Boolean;
	
	/**
	 * 프로젝타일 쏘기
	 *
	 * @return 쏜 프로젝타일의 개수 (실패하면 0)
	 */
	fun fire(target: Position, shooter: Entity): Int;
}
