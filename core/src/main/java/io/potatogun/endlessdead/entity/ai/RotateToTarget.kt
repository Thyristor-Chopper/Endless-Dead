package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 대상으로 몸을 돌린다.
 *
 * @property entity 사용 개체
 */
class RotateToTarget<T>(private val entity: T) : Behavior where T : Entity, T : Targetable {
	override fun update(delta: Float): Behavior.Result {
		val target: LivingEntity? = entity.target;
		if(target == null) return Behavior.Result.FAILED;

		entity.rotateTo(target);
		return Behavior.Result.SUCCEEDED;
	}
}
