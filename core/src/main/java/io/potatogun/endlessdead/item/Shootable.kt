package io.potatogun.endlessdead.item;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;

/**
 * '쏘기'가 가능한 아이템
 */
interface Shootable {
	/**
	 * 프로젝타일 쏘기
	 *
	 * @param target  쏠 위치
	 * @param shooter 발사한 개체
	 * @return        쏜 프로젝타일의 개수 (실패하면 0)
	 */
	fun shoot(target: Position, shooter: Entity): Int;
}
