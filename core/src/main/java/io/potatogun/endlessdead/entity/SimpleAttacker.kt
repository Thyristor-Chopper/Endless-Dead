package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * Attackable을 수동 위임하기 위해 사용된다.
 * 
 * @property owner         실제 공격하는 개체
 * @property followRange   공격 대상자 감지 범위
 * @property targetFetcher 새 공격 대상자를 찾는 함수
 * @throws IllegalArgumentException 감지 범위가 잘못됐을 때
 */
class SimpleAttacker(private val owner: Entity, private val followRange: Float = 0f, private var targetFetcher: () -> LivingEntity? = { null }) : Attackable {
	override var target: LivingEntity? = targetFetcher()
		get() {
			val target: LivingEntity? = field;
			if(!isValidTarget(target)) {
				val newTarget: LivingEntity? = targetFetcher();
				field = if(isValidTarget(newTarget)) newTarget else null;
				return field;
			}
			return target;
		};

	init {
		if(followRange < 0f) throw IllegalArgumentException("invalid follow range");
	}

	private inline fun isValidTarget(entity: LivingEntity?): Boolean = entity != null && entity.isAlive && (followRange == 0f || (followRange > 0f && entity.distanceTo(owner) <= followRange));

	/**
	 * 새 공격 대상자를 찾는 함수를 다시 등록한다.
	 *
	 * @param fetcher 찾기 함수
	 */
	fun setTargetFetcher(fetcher: () -> LivingEntity?) {
		targetFetcher = fetcher;
	}
}
