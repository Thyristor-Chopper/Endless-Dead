package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.gdxhelper.entity.Entity;

import kotlin.math.sqrt;

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
