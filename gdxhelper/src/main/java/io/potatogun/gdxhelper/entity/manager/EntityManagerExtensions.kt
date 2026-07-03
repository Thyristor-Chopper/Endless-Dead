@file:JvmName("EntityQueries")
package io.potatogun.gdxhelper.entity.manager;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.entity.Entity;

import kotlin.random.Random;

/**
 * 개체 중 아무거나 반환한다.
 *
 * @return 개체 (없으면 null)
 */
fun EntityManager.getRandom(): Entity? {
	if(view.isEmpty) return null;
	return view[Random.nextInt(view.size)];
}

/**
 * 지정한 종류의 개체들의 수를 반환한다. (코틀린 전용)
 *
 * @return 개체 수
 */
@JvmSynthetic inline fun <reified T : Entity> EntityManager.countOf(): Int = countOf(T::class.java);

/**
 * 지정한 종류의 개체들의 수를 반환한다. (자바 전용)
 *
 * @param type 개체 종류
 * @return 개체 수
 */
fun <T : Entity> EntityManager.countOf(type: Class<T>): Int {
	var ret = 0;
	for(i in 0 until view.size)
		if(type.isInstance(view[i]))
			ret++;
	return ret;
}

/**
 * 지정한 종류의 개체 중 처음으로 등록된 것을 반환한다. (코틀린 전용)
 *
 * @return 개체 (없으면 null)
 */
@JvmSynthetic inline fun <reified T : Entity> EntityManager.getFirstOf(): T? = getFirstOf(T::class.java);

/**
 * 지정한 종류의 개체 중 처음으로 등록된 것을 반환한다. (자바 전용)
 *
 * @param type 개체 종류
 * @return     개체 (없으면 null)
 */
fun <T : Entity> EntityManager.getFirstOf(type: Class<T>): T? {
	for(i in 0 until view.size) {
		val entity = view[i];
		if(type.isInstance(entity))
			return entity as T;
	}
	return null;
}

/**
 * 지정한 종류의 개체를 아무거나 반환한다. (코틀린 전용)
 *
 * @return 개체 (없으면 null)
 */
@JvmSynthetic inline fun <reified T : Entity> EntityManager.getRandomOf(): T? = getRandomOf(T::class.java);

/**
 * 지정한 종류의 개체를 아무거나 반환한다. (자바 전용)
 *
 * @param type 개체 종류
 * @return     개체 (없으면 null)
 */
fun <T : Entity> EntityManager.getRandomOf(type: Class<T>): T? {
	var count = 0;
	var ret: T? = null;
	for(i in 0 until view.size) {
		val entity = view[i];
		if(type.isInstance(entity) && Random.nextInt(++count) == 0)
			ret = entity as T;
	}
	return ret;
}

/**
 * 지정한 개체로부터 가장 가까운 것을 반환한다.
 *
 * @param entity 기준 개체
 * @return       개체 (없으면 null)
 */
fun EntityManager.getClosest(entity: Entity): Entity? {
	if(view.isEmpty) return null;
	var ret = view[0];
	var min = ret.distanceTo(entity);
	for(i in 0 until view.size) {
		val e = view[i];
		val distance = e.distanceTo(entity);
		if(distance < min) {
			min = distance;
			ret = e;
		}
	}
	return ret;
}

/**
 * 지정한 종류의 개체들 중 지정한 개체로부터 가장 가까운 것을 반환한다. (코틀린 전용)
 *
 * @param entity 기준 개체
 * @return       개체 (없으면 null)
 */
@JvmSynthetic inline fun <reified T : Entity> EntityManager.getClosestOf(entity: Entity): T? = getClosestOf(T::class.java, entity);

/**
 * 지정한 종류의 개체들 중 지정한 개체로부터 가장 가까운 것을 반환한다. (자바 전용)
 *
 * @param type   개체 종류
 * @param entity 기준 개체
 * @return       개체 (없으면 null)
 */
fun <T : Entity> EntityManager.getClosestOf(type: Class<T>, entity: Entity): T? {
	if(view.isEmpty) return null;
	var min: Float = 0f;  // X같은코틀린
	var ret: T? = getFirstOf(type)?.also { min = it.distanceTo(entity) };
	if(ret == null) return null;
	for(i in 0 until view.size) {
		val e = view[i];
		if(!type.isInstance(e)) continue;
		val distance = e.distanceTo(entity);
		if(distance < min) {
			min = distance;
			ret = e as T;
		}
	}
	return ret;
}

inline fun distanceComparator(entity: Entity): Comparator<Entity> = compareBy { it.distanceTo(entity) };

/**
 * 지정한 개체와 가까운 순으로 정렬된 개체 목록을 반환한다.
 *
 * @param entity 기준 개체
 * @return 개체 목록
 */
inline fun EntityManager.getDistanceSorted(entity: Entity): GdxArray<Entity> = view.sortedWith(distanceComparator(entity));

/**
 * 지정한 개체와 가까운 순으로 정렬된 개체 목록을 반환한다.
 *
 * @param entity 기준 개체
 * @param output 개체 목록
 */
inline fun EntityManager.getDistanceSorted(entity: Entity, output: GdxArray<Entity>) {
	view.sortedWith(distanceComparator(entity), output);
}
