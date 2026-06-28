package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;
import kotlin.reflect.KClass;

/**
 * 개체 목록 관리자
 */
interface EntityManager {
	val world: World;
	val view: View;

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
	 * 현재 개체의 주변 개체를 순회한다.
	 *
	 * @param entity 기준 개체
	 */
	fun forEachNearby(entity: Entity, callback: (Entity) -> Unit);

	/**
	 * 등록된 모든 객체를 그린다.
	 *
	 * 이렇게 해야 서브클래스의 draw() 는 '자기 위치에 그냥 그려라'만 구현하면 되고,
	 *   카메라가 움직이든 말든 신경 쓸 필요가 없다.
	 */
	fun draw(batch: SpriteBatch);

	/**
	 * 등록된 모든 개체에게 'update(delta) 한 프레임 진행'을 시킨다.
	 *   개체 관리자 자체도 갱신사항이 있다면 여기서 처리하면 된다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	fun update(delta: Float);

	/**
	 * 등록된 개체들의 자원을 해제한다.
	 */
	fun dispose();

	/**
	 * 개체 목록을 읽기 전용으로 상호작용할 수 있는 뷰이다.
	 */
	interface View {
		val size: Int;
		val isEmpty: Boolean;

		operator fun get(index: Int): Entity;

		fun sortedWith(comparator: Comparator<Entity>): List<Entity>;

		fun forEach(callback: (Entity) -> Unit);

		fun filter(condition: (Entity) -> Boolean): List<Entity>;

		fun filter(condition: (Entity) -> Boolean, output: MutableList<Entity>);
	}
}

// 확장 함수

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
 * 지정한 종류의 개체들의 수를 반환한다.
 *
 * @return 개체 수
 */
inline fun <reified T : Entity> EntityManager.countOf(): Int = countOf(T::class);

fun <T : Entity> EntityManager.countOf(type: KClass<T>): Int {
	var ret = 0;
	for(i in 0 until view.size)
		if(type.isInstance(view[i]))
			ret++;
	return ret;
}

/**
 * 지정한 종류의 개체 중 처음으로 등록된 것을 반환한다.
 *
 * @return 개체 (없으면 null)
 */
inline fun <reified T : Entity> EntityManager.getFirstOf(): T? = getFirstOf(T::class);

fun <T : Entity> EntityManager.getFirstOf(type: KClass<T>): T? {
	for(i in 0 until view.size) {
		val entity = view[i];
		if(type.isInstance(entity))
			return entity as T;
	}
	return null;
}

/**
 * 지정한 종류의 개체를 아무거나 반환한다. reified의 요구조건 때문에 inline이다.
 *
 * @return 개체 (없으면 null)
 */
inline fun <reified T : Entity> EntityManager.getRandomOf(): T? = getRandomOf(T::class);

fun <T : Entity> EntityManager.getRandomOf(type: KClass<T>): T? {
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
 * 지정한 종류의 개체들 중 지정한 개체로부터 가장 가까운 것을 반환한다.
 *
 * @param entity 기준 개체
 * @return       개체 (없으면 null)
 */
inline fun <reified T : Entity> EntityManager.getClosestOf(entity: Entity): T? = getClosestOf(entity, T::class);

fun <T : Entity> EntityManager.getClosestOf(entity: Entity, type: KClass<T>): T? {
	if(view.isEmpty) return null;
	var ret: T? = null;
	var min = Utils.max2(world.width, world.height);
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

fun EntityManager.getDistanceSorted(entity: Entity): List<Entity> = view.sortedWith(compareBy { it.distanceTo(entity) });
