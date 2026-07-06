package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.MeleeAttackable;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 근접 공격 컴포넌트
 *
 * @property attacker       공격자
 * @property attackDamage   공격력
 * @property attackInterval 공격 속도
 */
class MeleeAttackComponent(private val attacker: Entity, override val attackDamage: Int, override val attackInterval: Float) : MeleeAttackable {
	private var attackCooldownTimer = 0f;

	/**
	 * 매 프레임 상태를 갱신한다.
	 *
	 * @param delta 직전 프레임과의 간격(초)
	 */
	fun update(delta: Float) {
		if(attackCooldownTimer > 0f)
			attackCooldownTimer -= delta;
	}

	override fun damageTarget(target: LivingEntity): Boolean {
		if(attackCooldownTimer > 0f) return false;

		val result = target.takeDamage(attackDamage, attacker = attacker);
		if(result) attackCooldownTimer = attackInterval;
		return result;
	}

	override fun meleeAttackNearby() {
		attacker.forEachNearby { entity ->
			if(entity is LivingEntity && attacker.collidesWith(entity))
				damageTarget(entity);
		};
	}
}
