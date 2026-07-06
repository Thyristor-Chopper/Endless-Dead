package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.MeleeAttackable;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 대상에게 다가가서 근접 공격한다.
 *
 * @property attacker              공격자
 * @property targetCenterGapFactor 대상의 크기(변 길이)의 이 곱까지는 간격을 둬도 공격할 수 있다.
 */
class MeleeAttackTarget<T>(private val attacker: T, private val targetCenterGapFactor: Float = 0f) : Behavior where T : Entity, T : Targetable, T : MeleeAttackable {
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
		if(distance > targetAverageLength * targetCenterGapFactor) {
			state = State.TOO_FAR;
			return Behavior.Result.REJECTED;
		}

		state = State.ATTACKING;
		if(attacker.damageTarget(target))
			return Behavior.Result.SUCCEEDED;
		else
			return Behavior.Result.FAILED;
	}

	/**
	 * 현재 AI 상태 열거형
	 */
	enum class State {
		STANDBY,
		ATTACKING,
		TOO_FAR;
	}
}
