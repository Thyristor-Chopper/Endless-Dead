package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.Position;
import io.potatogun.endlessdead.entity.Entity;

/**
 * '쏘기'가 가능한 아이템
 */
interface Fireable {
	val bulletDamage: Int;
	val bulletSpeed: Float;
	val penetrable: Boolean;
	val bulletHp: Int;
	
	/**
	 * 총 쏘기
	 *
	 * @return 발사 성공 여부
	 */
	fun fire(target: Position, shooter: Entity): Boolean;
}
