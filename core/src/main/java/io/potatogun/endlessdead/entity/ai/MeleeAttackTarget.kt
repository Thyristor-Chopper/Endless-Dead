package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Mob;

/**
 * 대상에게 다가가서 근접 공격한다.
 *
 * @property attacker              공격자
 * @property targetCenterGapFactor 대상의 크기(변 길이)의 이 곱까지는 간격을 둬도 공격할 수 있다.
 */
class MeleeAttackTarget(private val attacker: Mob, private val targetCenterGapFactor: Float = 0f) : Behavior {
	private val approacher = ApproachTarget(attacker, targetCenterGapFactor = targetCenterGapFactor);
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		approacher.update(delta);
		if(approacher.state != ApproachTarget.State.APPROACHED) {
			state = State.TOO_FAR;
			return Behavior.Result.REJECTED;
		}

		state = State.ATTACKING;
		if(attacker.damageTarget())
			return Behavior.Result.SUCCEEDED;
		else
			return Behavior.Result.FAILED;
	}

	/**
	 * 현재 AI 상태
	 */
	enum class State {
		STANDBY,
		ATTACKING,
		TOO_FAR;
	}
}
