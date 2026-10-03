package io.potatogun.endlessdead.entity.ai;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Movable;
import io.potatogun.endlessdead.entity.Targetable;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.gdxhelper.entity.Entity;

/**
 * 대상에게 돌진한다.
 *
 * @property attacker    공격자
 * @property dashDamage  돌진 시 피해량
 * @property dashSpeed   돌진 속도
 * @property minDistance 돌진하기 위해 접근해야 할 최소 거리
 */
class DashToTarget<T>(private val attacker: T, private val dashDamage: Int, private val dashSpeed: Float, private val minDistance: Float) : Behavior() where T : Entity, T : Targetable, T : Movable {
	/**
	 * 현재 AI 상태
	 */
	var state = State.STANDBY
		private set;
	private var stateTimer = 0f;
	// 돌진할 '방향(벡터)'을 기억해 둘 변수
	private var dashDirX = 0f;
	private var dashDirY = 0f;
	private val dashComponent = MoveComponent(attacker, dashSpeed);

	override fun update(delta: Float) {
		val target: LivingEntity? = attacker.target;
		if(target == null) {
			lastResult = Behavior.Result.FAILED;
			return;
		}

		// 원래 when 문으로 돼 있었는데 코틀린 컴파일러가 멍청해서 이걸 그냥 if(state == State.XXX) ... else ...로 안 바꾸고 쓰잘데기없이 WhenMappings같은 이상한 거 만들어서 if문으로 변경
		if(state == State.STANDBY) {
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
				lastResult = Behavior.Result.REJECTED;
				return;
			}
		} else if(state == State.PREPARING) {
			stateTimer -= delta;
			if(stateTimer <= 0f) {
				state = State.DASHING;
				stateTimer = 0.4f;
			}
		} else if(state == State.DASHING) {
			dashComponent.move(delta, dashDirX, dashDirY);

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
		} else if(state == State.COOLDOWN) {
			stateTimer -= delta;
			if(stateTimer <= 0f)
				state = State.STANDBY;
		}

		lastResult = Behavior.Result.SUCCEEDED;
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
