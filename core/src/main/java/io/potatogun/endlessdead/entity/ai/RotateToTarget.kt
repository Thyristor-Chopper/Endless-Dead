package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 대상으로 몸을 돌린다.
 *
 * @property entity 사용 개체
 */
class RotateToTarget<T>(private val entity: T) : Behavior() where T : Entity, T : Targetable {
	override fun update(delta: Float) {
		val target: LivingEntity? = entity.target;
		if(target == null) {
			lastResult = Behavior.Result.FAILED;
			return;
		}

		entity.rotateTo(target);
		lastResult = Behavior.Result.SUCCEEDED;
	}
}
