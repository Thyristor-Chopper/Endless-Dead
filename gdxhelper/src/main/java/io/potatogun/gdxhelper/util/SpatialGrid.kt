package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * 월드 좌표에 따라 격자로 분할하여 관리하는 개체 관리자
 */
interface SpatialGrid : EntityManager {
	/**
	 * 개체가 속한 타일을 갱신한다.
	 *
	 * @param entity 갱신할 개체
	 */
	fun updateTile(entity: Entity);

	/**
	 * 현재 개체의 주변 개체를 나열한다.
	 *
	 * @param entity 기준 개체
	 */
	fun getNearby(entity: Entity): List<Entity>;
}
