package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.AttackTargetable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.gdxhelper.entity.Entity;

class DashToTarget(private val attacker: AttackTargetable, private val dashSpeed: Float, private val dashThreshold: Float) : Behavior {
	var state = DashState.STANDBY
		private set;
	private var stateTimer = 0f;
	// 돌진할 '방향(벡터)'을 기억해 둘 변수
	private var dashDirX = 0f;
	private var dashDirY = 0f;

	init {
		if(attacker !is Entity)
			throw IllegalArgumentException("attacker is not an entity");
		if(attacker !is Movable)
			throw IllegalArgumentException("attacker is not movable");
	}

	override fun update(delta: Float): Behavior.Result {
		if(attacker !is Entity) return Behavior.Result.FAILED;
		if(attacker !is Movable) return Behavior.Result.FAILED;
		val target: LivingEntity? = attacker.target;
		if(target == null) return Behavior.Result.FAILED;

		when(state) {
			DashState.STANDBY -> {
				val distance = attacker.distanceTo(target);
				if(distance < dashThreshold) {
					state = DashState.PREPARING;
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
			DashState.PREPARING -> {
				stateTimer -= delta;
				if(stateTimer <= 0f) {
					state = DashState.DASHING;
					stateTimer = 0.4f;
				}
			}
			DashState.DASHING -> {
				attacker.x += dashDirX * dashSpeed * delta;
				attacker.y += dashDirY * dashSpeed * delta;

				// 돌진 중에 플레이어랑 부딪히면 대미지 주고 즉시 쿨타임으로 넘어감
				if(attacker.collidesWith(target)) {
					target.takeDamage(20, attacker = attacker);
					state = DashState.COOLDOWN;
					stateTimer = 5.0f;
				} else {
					stateTimer -= delta;
					if(stateTimer <= 0f) {
						state = DashState.COOLDOWN;
						stateTimer = 5.0f;
					}
				}
			}
			DashState.COOLDOWN -> {
				stateTimer -= delta;
				if(stateTimer <= 0f)
					state = DashState.STANDBY;
			}
		}

		return Behavior.Result.SUCCEEDED;
	}

	/**
	 * 평상시, 돌진하려고 잠깐 멈춰있음, 돌진, 돌진 쿨
	 */
	enum class DashState {
		STANDBY,
		PREPARING,
		DASHING,
		COOLDOWN;
	}
}
