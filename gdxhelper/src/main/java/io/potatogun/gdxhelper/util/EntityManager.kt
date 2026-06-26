package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * 개체 목록 관리자
 */
interface EntityManager {
	/**
	 * 개체를 등록한다.
	 *
	 * @param entity 등록할 개체
	 * @return       성공 여부(중복 시 실패)
	 */
	fun add(entity: Entity): Boolean;

	/**
	 * 개체를 제거한다.
	 *
	 * @param entity 제거할 개체
	 * @return       성공 여부
	 */
	fun remove(entity: Entity): Boolean;

	/**
	 * 개체가 이동하면 이동된 위치에 맞게 저장된 데이타를 갱신한다.
	 *
	 * @param entity 갱신할 개체
	 */
	fun updatePosition(entity: Entity) {}

	/**
	 * 등록된 모든 개체 목록을 반환한다.
	 *
	 * @return 개체 목록
	 */
	fun getAll(): List<Entity>;

	/**
	 * 현재 개체의 주변 개체를 나열한다.
	 *
	 * @param entity 기준 개체
	 */
	fun getNearby(entity: Entity): List<Entity>;

	/**
	 * 등록된 개체들의 자원을 해제한다.
	 */
	fun dispose();
}

// 확장 함수

/**
 * 지정한 종류의 개체들의 목록을 반환한다.
 *
 * @return 개체 목록
 */
inline fun <reified T : Entity> EntityManager.getAllOf(): List<T> = getAll().filterIsInstance<T>();

/**
 * 지정한 종류의 개체 중 처음으로 등록된 것을 반환한다.
 *
 * @return 개체 (없으면 null)
 */
inline fun <reified T : Entity> EntityManager.get(): T? = getAll().firstOrNull { it is T } as T?;

/**
 * 지정한 종류의 개체를 아무거나 반환한다.
 *
 * @return 개체 (없으면 null)
 */
inline fun <reified T : Entity> EntityManager.getRandom(): T? = getAll().shuffled().firstOrNull { it is T } as T?;

/**
 * 지정한 개체로부터 거리순으로 정렬한다.
 *
 * @param entity 기준 개체
 */
fun EntityManager.getDistanceSorted(entity: Entity): List<Entity> = getAll().sortedWith(compareBy { it.distanceTo(entity) });
