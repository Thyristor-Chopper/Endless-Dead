package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Mob;

/**
 * 대상에게 돌진한다.
 *
 * @property attacker    공격자
 * @property dashDamage  돌진 시 피해량
 * @property dashSpeed   돌진 속도
 * @property minDistance 돌진하기 위해 접근해야 할 최소 거리
 */
class DashToTarget(private val attacker: Mob, private val dashDamage: Int, private val dashSpeed: Float, private val minDistance: Float) : Behavior {
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;
	private var stateTimer = 0f;
	// 돌진할 '방향(벡터)'을 기억해 둘 변수
	private var dashDirX = 0f;
	private var dashDirY = 0f;

	override fun update(delta: Float): Behavior.Result {
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		when(state) {
			State.STANDBY -> {
				val distance = attacker.distanceTo(target);
				if(distance < minDistance) {
					state = State.PREPARING;
					stateTimer = 0.5f;

					// 대기 상태에 들어가는 첫 프레임. 이때 플레이어를 조준해서 방향을 기억해둔다
					val dx = target.x - attacker.x;
					val dy = target.y - attacker.y;
					if(distance > 0) {
						dashDirX = dx / distance;
						dashDirY = dy / distance;
					}
				} else {
					return Behavior.Result.REJECTED;
				}
			}
			State.PREPARING -> {
				stateTimer -= delta;
				if(stateTimer <= 0f) {
					state = State.DASHING;
					stateTimer = 0.4f;
				}
			}
			State.DASHING -> {
				attacker.x += dashDirX * dashSpeed * delta;
				attacker.y += dashDirY * dashSpeed * delta;

				// 돌진 중에 플레이어랑 부딪히면 대미지 주고 즉시 쿨타임으로 넘어감
				if(attacker.collidesWith(target)) {
					target.takeDamage(dashDamage, attacker = attacker);
					state = State.COOLDOWN;
					stateTimer = 5.0f;
				} else {
					stateTimer -= delta;
					if(stateTimer <= 0f) {
						state = State.COOLDOWN;
						stateTimer = 5.0f;
					}
				}
			}
			State.COOLDOWN -> {
				stateTimer -= delta;
				if(stateTimer <= 0f)
					state = State.STANDBY;
			}
		}

		return Behavior.Result.SUCCEEDED;
	}

	/**
	 * 현재 AI 상태
	 *   평상시, 돌진하려고 잠깐 멈춰있음, 돌진, 돌진 쿨
	 */
	enum class State {
		STANDBY,
		PREPARING,
		DASHING,
		COOLDOWN;
	}
}
