package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.entity.Entity;

import kotlin.reflect.KClass;

/**
 * 개체 좌표계를 나누는 그리드이다.
 */
interface SpatialGrid {
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

	/**
	 * 등록된 모든 개체 목록을 반환한다.
	 *
	 * @return 개체 목록
	 */
	fun getAll(): List<Entity>;

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
inline fun <reified T : Entity> SpatialGrid.getAllOf(): List<T> = getAllOf(T::class);

fun <T : Entity> SpatialGrid.getAllOf(type: KClass<T>): List<T> = this.getAll().filterIsInstance(type.java);

/**
 * 지정한 종류의 개체 중 처음으로 등록된 것을 반환한다.
 *
 * @return 개체 (없으면 null)
 */
inline fun <reified T : Entity> SpatialGrid.get(): T? = get(T::class);

fun <T : Entity> SpatialGrid.get(type: KClass<T>): T? = this.getAll().firstOrNull { it::class == type } as T?;

/**
 * 지정한 종류의 개체를 아무거나 반환한다.
 *
 * @return 개체 (없으면 null)
 */
inline fun <reified T : Entity> SpatialGrid.getRandom(): T? = getRandom(T::class);

fun <T : Entity> SpatialGrid.getRandom(type: KClass<T>): T? = this.getAll().shuffled().firstOrNull { it::class == type } as T?;
