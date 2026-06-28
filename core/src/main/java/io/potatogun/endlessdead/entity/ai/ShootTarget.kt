package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Mob;
import io.potatogun.endlessdead.item.Shootable;

/**
 * 대상에게 발사체를 쏜다.
 *
 * @property attacker       공격자
 * @property targetDistance 얼만큼 다가갈지의 값 (0: 쏘기 전에 굳이 다가가지 않음)
 */
class ShootTarget(private val attacker: Mob, private val targetDistance: Float = 0f) : Behavior {
	private val approacher: ApproachTarget? = if(targetDistance > 0f) ApproachTarget(attacker, targetDistance) else null;
	var state = State.STANDBY
		private set;

	init {
		if(attacker !is ItemSelectable) throw IllegalArgumentException("attacker cannot hold any shootables");
	}

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		if(attacker !is ItemSelectable) return Behavior.Result.FAILED;
		val item = attacker.selectedItem;
		if(item !is Shootable) return Behavior.Result.REJECTED;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		approacher?.let {
			it.update(delta);
			if(it.state != ApproachTarget.State.APPROACHED) {
				state = State.TOO_FAR;
				return Behavior.Result.REJECTED;
			}
		};

		state = State.SHOOTING;
		if(item.shoot(target.position, attacker) > 0)
			return Behavior.Result.SUCCEEDED;
		else
			return Behavior.Result.FAILED;
	}

	/**
	 * 현재 AI 상태
	 */
	enum class State {
		STANDBY,
		SHOOTING,
		TOO_FAR;
	}
}
