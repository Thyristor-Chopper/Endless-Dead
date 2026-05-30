package com.oop.game.item;

import com.oop.game.Position;
import com.oop.game.entity.Entity;

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
