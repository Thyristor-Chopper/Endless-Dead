package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.AttackTargetable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.MeleeAttacker;
import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 대상을 근접 공격한다.
 *
 * @property attacker              공격자
 * @property targetCenterGapFactor 대상의 크기(변 길이)의 이 곱까지는 간격을 둬도 공격할 수 있다.
 */
class MeleeAttackTarget(private val attacker: AttackTargetable, private val targetCenterGapFactor: Float = 0f) : Behavior {
	var state = State.STANDBY
		private set;
	private var attackCooldownTimer = 0f;

	init {
		if(attacker !is Entity)
			throw IllegalArgumentException("attacker is not an entity");
		if(attacker !is MeleeAttacker)
			throw IllegalArgumentException("attacker is not a melee attacker");
		attackCooldownTimer = attacker.attackInterval;
	}

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		if(attacker !is Entity) return Behavior.Result.FAILED;
		if(attacker !is MeleeAttacker) return Behavior.Result.FAILED;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		val distance = attacker.distanceTo(target);
		val targetAverageLength = (target.width + target.height) * 0.5f;

		if(distance <= targetAverageLength * targetCenterGapFactor) {
			state = State.ATTACKING;

			attackCooldownTimer -= delta;
			if(attackCooldownTimer <= 0f) {
				attackCooldownTimer = attacker.attackInterval;
				target.takeDamage(attacker.attackDamage, attacker = attacker);
			}
			return Behavior.Result.SUCCEEDED;
		} else {
			state = State.TOO_FAR;
			attackCooldownTimer = 0f;
			return Behavior.Result.REJECTED;
		}
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
