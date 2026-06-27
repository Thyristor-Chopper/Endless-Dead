package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.AttackTargetable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.gdxhelper.entity.Entity;

class ApproachTarget(private val attacker: AttackTargetable, private val minDistance: Float = 0f, private val targetCenterGapFactor: Float = 0f) : Behavior {
	var state = State.STANDBY
		private set;

	init {
		if(attacker !is Entity)
			throw IllegalArgumentException("attacker is not an entity");
		if(attacker !is Movable)
			throw IllegalArgumentException("attacker is not movable");
	}

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		if(attacker !is Entity) return Behavior.Result.FAILED;
		if(attacker !is Movable) return Behavior.Result.FAILED;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		val distance = attacker.distanceTo(target);
		val targetAverageLength = (target.width + target.height) * 0.5f;

		if(distance <= minDistance + targetAverageLength * targetCenterGapFactor) {
			state = State.APPROACHED;
			return Behavior.Result.REJECTED;
		} else {
			state = State.APPROACHING;

			val dx = target.x - attacker.x;
			val dy = target.y - attacker.y;
			val speed = attacker.speed;
			attacker.x += dx / distance * speed * delta;
			attacker.y += dy / distance * speed * delta;

			return Behavior.Result.SUCCEEDED;
		}
	}

	enum class State {
		STANDBY,
		APPROACHING,
		APPROACHED;
	}
}
