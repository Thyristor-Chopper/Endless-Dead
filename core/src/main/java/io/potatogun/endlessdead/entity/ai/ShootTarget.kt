package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.endlessdead.item.Shootable;

/**
 * 대상에게 일정 거리까지 접근하고 발사체를 쏜다.
 *
 * @property attacker       공격자
 * @property targetDistance 얼만큼 다가갈지의 값 (0: 쏘기 전에 굳이 다가가지 않음)
 */
class ShootTarget<T>(private val attacker: T, private val targetDistance: Float = 0f) : Behavior where T : Entity, T : Targetable, T : ItemSelectable {
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;

	init {
		if(targetDistance < 0f)
			throw IllegalArgumentException("invalid target distance");
	}

	override fun update(delta: Float): Behavior.Result {
		state = State.STANDBY;
		val item = attacker.selectedItem;
		if(item !is Shootable) return Behavior.Result.REJECTED;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		if(targetDistance > 0f && attacker.distanceTo(target) > targetDistance) {
			state = State.TOO_FAR;
			return Behavior.Result.REJECTED;
		}

		state = State.SHOOTING;
		if(item.shoot(target.position, attacker) > 0)
			return Behavior.Result.SUCCEEDED;
		else
			return Behavior.Result.FAILED;
	}

	/**
	 * 현재 AI 상태 열거형
	 */
	enum class State {
		STANDBY,
		SHOOTING,
		TOO_FAR;
	}
}
