package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.endlessdead.item.Shootable;

/**
 * 대상에게 일정 거리까지 접근하고 발사체를 쏜다.
 *
 * @property attacker 공격자
 */
class ShootTarget<T>(private val attacker: T) : Behavior where T : Entity, T : Targetable, T : ItemSelectable {
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;

	override fun update(delta: Float) {
		state = State.STANDBY;
		val item = attacker.selectedItem;
		if(item !is Shootable) return;
		val target: LivingEntity? = attacker.target;
		if(target == null) return;

		state = State.SHOOTING;
		item.shoot(target.position, attacker);
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
