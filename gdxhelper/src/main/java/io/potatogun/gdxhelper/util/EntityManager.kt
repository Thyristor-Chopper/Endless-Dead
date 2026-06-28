package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

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

		fun sortedWith(comparator: Comparator<Entity>): GdxArray<Entity>;

		fun sortedWith(comparator: Comparator<Entity>, output: GdxArray<Entity>);

		fun filter(condition: (Entity) -> Boolean): GdxArray<Entity>;

		fun filter(condition: (Entity) -> Boolean, output: GdxArray<Entity>);

		fun clone(): GdxArray<Entity>;

		fun clone(output: GdxArray<Entity>);

		fun forEach(callback: (Entity) -> Unit);
	}
}
