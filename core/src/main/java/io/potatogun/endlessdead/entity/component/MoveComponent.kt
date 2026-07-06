package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.gdxhelper.entity.Entity;

import kotlin.math.sqrt;

/**
 * 매 프레임당 일정한 속도로 이동하는 컴포넌트
 *
 * @property entity 이동자
 * @property speed  이동 속도
 */
class MoveComponent(private val entity: Entity, override val speed: Float) : Movable {
	/**
	 * 속도 배수
	 */
	var speedModifier = 1f;
	/**
	 * 속도 증감치
	 */
	var speedAddend = 0f;

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		val length = sqrt(directionX * directionX + directionY * directionY);
		if(length == 0f) return;

		val speed = this.speed * speedModifier + speedAddend;
		entity.position.addX(directionX / length * speed * delta);
		entity.position.addY(directionY / length * speed * delta);
	}
}
