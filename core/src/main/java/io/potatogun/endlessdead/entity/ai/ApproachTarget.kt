package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Mob;

/**
 * 공격 대상에게 다가간다.
 *
 * @property attacker              공격자
 * @property targetDistance        지정한 거리만큼 좁혀질 때까지 다가간다.
 * @property targetCenterGapFactor 대상의 크기(변 길이)의 이 곱만큼 간격을 둔다.
 */
class ApproachTarget(private val attacker: Mob, private val targetDistance: Float = 0f, private val targetCenterGapFactor: Float = 0f) : Behavior {
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		val distance = attacker.distanceTo(target);
		val targetAverageLength = (target.width + target.height) * 0.5f;

		if(distance <= targetDistance + targetAverageLength * targetCenterGapFactor) {
			state = State.APPROACHED;
			return Behavior.Result.REJECTED;
		} else {
			state = State.APPROACHING;

			val dx = target.x - attacker.x;
			val dy = target.y - attacker.y;
			val speed = attacker.movementSpeed;
			attacker.x += dx / distance * speed * delta;
			attacker.y += dy / distance * speed * delta;

			return Behavior.Result.SUCCEEDED;
		}
	}

	/**
	 * 현재 AI 상태
	 */
	enum class State {
		STANDBY,
		APPROACHING,
		APPROACHED;
	}
}
