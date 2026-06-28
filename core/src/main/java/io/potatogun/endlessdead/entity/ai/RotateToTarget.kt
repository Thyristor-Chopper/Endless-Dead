package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Mob;

/**
 * 공격 대상으로 몸을 돌린다.
 *
 * @property attacker 공격자
 */
class RotateToTarget(private val attacker: Mob) : Behavior {
	override fun update(delta: Float): Behavior.Result {
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		attacker.rotateTo(target);
		return Behavior.Result.SUCCEEDED;
	}
}
