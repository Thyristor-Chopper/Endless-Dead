package io.potatogun.gdxhelper.entity.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.collections.View;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.util.function.Consumer;

/**
 * 개체 관리자 - API만 지키면 쿼드트리같은 것도 만들 수 있다.
 */
interface EntityManager {
	/**
	 * 개체 목록 읽기 전용 상호작용 도구 - 자바에서는 .view()로 접근
	 */
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("view") val view: View<Entity>;

	/**
	 * 개체를 등록한다.
	 *
	 * @param entity 등록할 개체
	 * @return       성공 여부(중복 시 실패)
	 * @throws IllegalArgumentException 추가하려는 개체가 다른 월드에 속해 있을 때
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
	 * 현재 개체의 주변 개체를 순회한다 - 자바 개발자도 편하게 구현할 수 있도록 Consumer를 사용하며 코틀린에서 호출할 때는 일반 { x -> ... } 람다로 여전히 호출 가능하다.
	 *
	 * @param entity   기준 개체
	 * @param callback 실행할 서브루틴
	 */
	fun forEachNearby(entity: Entity, callback: Consumer<Entity>);

	/**
	 * 등록된 모든 객체를 그린다.
	 *
	 * 이렇게 해야 서브클래스의 draw()는 '자기 위치에 그냥 그려라'만 구현하면 되고,
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
}
