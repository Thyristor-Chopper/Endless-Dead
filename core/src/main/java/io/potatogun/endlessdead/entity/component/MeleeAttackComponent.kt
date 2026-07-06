package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.MeleeAttackable;
import io.potatogun.gdxhelper.entity.Entity;

class MeleeAttackComponent(private val attacker: Entity, override val attackDamage: Int, override val attackInterval: Float) : MeleeAttackable {
	private var attackCooldownTimer = 0f;

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
