package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.Position;
import io.potatogun.endlessdead.entity.Entity;

/**
 * '쏘기'가 가능한 아이템
 */
interface Fireable {
	/**
	 * 프로젝타일 쏘기
	 *
	 * @return 쏜 프로젝타일의 개수 (실패하면 0)
	 */
	fun fire(target: Position, shooter: Entity): Int;
}
