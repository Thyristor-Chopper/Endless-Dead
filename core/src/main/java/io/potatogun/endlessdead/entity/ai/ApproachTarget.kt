package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 공격 대상에게 다가간다.
 *
 * @property targeter              공격자
 * @property targetDistance        지정한 거리만큼 좁혀질 때까지 다가간다.
 * @property targetCenterGapFactor 대상의 크기(변 길이)의 이 곱만큼 간격을 둔다.
 */
class ApproachTarget<T>(private val targeter: T, private val targetDistance: Float = 0f, private val targetCenterGapFactor: Float = 0f) : Behavior where T : Entity, T : Targetable, T : Movable {
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		val target: LivingEntity? = targeter.target;
		if(target == null) return Behavior.Result.FAILED;

		val distance = targeter.distanceTo(target);
		val targetAverageLength = (target.width + target.height) * 0.5f;
		if(distance <= targetDistance + targetAverageLength * targetCenterGapFactor) {
			state = State.APPROACHED;
			return Behavior.Result.REJECTED;
		} else {
			state = State.APPROACHING;
			val dx = target.x - targeter.x;
			val dy = target.y - targeter.y;
			targeter.move(delta, dx / distance, dy / distance);
			return Behavior.Result.SUCCEEDED;
		}
	}

	/**
	 * 현재 AI 상태 열거형
	 */
	enum class State {
		STANDBY,
		APPROACHING,
		APPROACHED;
	}
}
