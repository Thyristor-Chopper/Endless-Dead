package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 개체 관리자
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
	 * 현재 개체의 주변 개체를 순회한다 (자바에서 사용).
	 *
	 * @param entity 기준 개체
	 */
	fun forEachNearby(entity: Entity, callback: Consumer<Entity>) {
		forEachNearby(entity) { callback.accept(it) };
	}

	/**
	 * 현재 개체의 주변 개체를 순회한다.
	 *
	 * @param entity 기준 개체
	 */
	@JvmSynthetic fun forEachNearby(entity: Entity, callback: (Entity) -> Unit);

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
		/**
		 * 개체 목록 크기
		 */
		val size: Int;
		/**
		 * 개체 목록이 비어 있는지의 여부
		 */
		val isEmpty: Boolean;

		/**
		 * 지정한 인덱스의 개체를 가져온다.
		 *
		 * @param index 인덱스
		 * @return      개체
		 */
		operator fun get(index: Int): Entity;

		/**
		 * 개체 목록을 지정한 비교기로 정렬한다.
		 *
		 * @param comparator 비교기
		 * @return           정렬된 목록
		 */
		fun sortedWith(comparator: Comparator<Entity>): GdxArray<Entity>;

		/**
		 * 개체 목록을 지정한 비교기로 정렬한다.
		 *
		 * @param comparator 비교기
		 * @param output     정렬 결과를 저장할 목록 (이미 다른 원소가 있다면 덮어씌워짐)
		 */
		fun sortedWith(comparator: Comparator<Entity>, output: GdxArray<Entity>);

		/**
		 * 지정한 조건에 해당하는 개제만 모은다 (자바에서 사용).
		 *
		 * @param condition 조건
		 * @return 결과 목록
		 */
		fun filter(condition: Function<Entity, Boolean>): GdxArray<Entity> = filter { condition.apply(it) };

		/**
		 * 지정한 조건에 해당하는 개제만 모은다.
		 *
		 * @param condition 조건
		 * @return 결과 목록
		 */
		@JvmSynthetic fun filter(condition: (Entity) -> Boolean): GdxArray<Entity>;

		/**
		 * 지정한 조건에 해당하는 개제만 모은다 (자바에서 사용).
		 *
		 * @param condition 조건
		 * @param output    결과를 저장할 목록 (이미 다른 원소가 있다면 덮어씌워짐)
		 */
		fun filter(condition: Function<Entity, Boolean>, output: GdxArray<Entity>) {
			filter({ condition.apply(it) }, output);
		}

		/**
		 * 지정한 조건에 해당하는 개제만 모은다.
		 *
		 * @param condition 조건
		 * @param output    결과를 저장할 목록 (이미 다른 원소가 있다면 덮어씌워짐)
		 */
		@JvmSynthetic fun filter(condition: (Entity) -> Boolean, output: GdxArray<Entity>);

		/**
		 * 개체 목록을 새 배열로 복사한다.
		 *
		 * @return 복사된 배열
		 */
		fun clone(): GdxArray<Entity>;

		/**
		 * 개체 목록을 지정한 배열로 복사한다.
		 *
		 * @param output 대상 배열 (기존 원소는 덮어씌워짐)
		 */
		fun clone(output: GdxArray<Entity>);

		/**
		 * 모든 개체를 순회한다 (자바에서 사용).
		 *
		 * @param callback 이번 개체에 대해 실행할 서브루틴
		 */
		fun forEach(callback: Consumer<Entity>) {
			forEach { callback.accept(it) };
		}

		/**
		 * 모든 개체를 순회한다.
		 *
		 * @param callback 이번 개체에 대해 실행할 서브루틴
		 */
		@JvmSynthetic fun forEach(callback: (Entity) -> Unit);
	}
}
