package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * AttackTargetable을 직접 위임하기 위해 사용된다.
 * 
 * @property owner         실제 공격자
 * @property followRange   공격 대상자 감지 범위
 * @property targetFetcher 새 공격 대상자를 찾는 함수
 * @throws IllegalArgumentException 감지 범위가 잘못됐을 때
 */
class AutoTargeter(private val owner: Entity, private var followRange: Float = 0f, private var targetFetcher: () -> LivingEntity? = { null }) : AttackTargetable {
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

	private inline fun isValidTarget(entity: LivingEntity?): Boolean = entity != null && entity.isAlive && !owner.isSameTeamWith(entity) && (followRange == 0f || (followRange > 0f && entity.distanceTo(owner) <= followRange));

	/**
	 * 새 공격 대상자를 찾는 함수를 다시 등록한다.
	 *
	 * @param fetcher 찾기 함수
	 */
	fun setTargetFetcher(fetcher: () -> LivingEntity?) {
		targetFetcher = fetcher;
	}

	/**
	 * 공격 대상 탐색 범위를 변경한다.
	 *
	 * @param range 대상 탐색 범위
	 */
	fun setFollowRange(range: Float) {
		if(range < 0f) throw IllegalArgumentException("invalid follow range");
		followRange = range;
	}
}
