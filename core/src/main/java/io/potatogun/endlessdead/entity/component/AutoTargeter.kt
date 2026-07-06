package io.potatogun.endlessdead.entity.component;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.endlessdead.entity.isSameTeamWith;
import io.potatogun.gdxhelper.entity.Entity;

import java.util.function.Supplier;

/**
 * 대상을 자동으로 찾아준다.
 * 
 * @property owner         실제 공격자
 * @property followRange   공격 대상자 감지 범위
 * @property findNewTarget 새 공격 대상자를 찾는 함수
 * @throws IllegalArgumentException 감지 범위가 잘못됐을 때
 */
class AutoTargeter @JvmOverloads constructor(private val owner: Entity, override var followRange: Float = 0f, private var findNewTarget: Supplier<LivingEntity?>) : Targetable {
	override var target: LivingEntity? = findNewTarget.get()
		get() {
			val target: LivingEntity? = field;
			if(!isValidTarget(target)) {
				val newTarget: LivingEntity? = findNewTarget.get();
				field = if(isValidTarget(newTarget)) newTarget else null;
				return field;
			}
			return target;
		};

	init {
		if(followRange < 0f) throw IllegalArgumentException("invalid follow range");
	}

	private inline fun isValidTarget(entity: LivingEntity?): Boolean = entity != null && entity.isAlive && !entity.isInvincible && !owner.isSameTeamWith(entity) && (followRange == 0f || (followRange > 0f && entity.distanceTo(owner) <= followRange));
}
