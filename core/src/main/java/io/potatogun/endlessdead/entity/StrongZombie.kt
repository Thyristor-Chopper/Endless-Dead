package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.world.World;

class StrongZombie(world: World, x: Float, y: Float) : Zombie(world, x, y, 49f, 70f, Zombie.Properties(15, 5, 50f)) {
	private var dashState = DashState.WALKING;
	private var stateTimer = 0f;
	// 돌진할 '방향(벡터)'을 기억해 둘 변수
	private var dashDirX = 0f;
	private var dashDirY = 0f;
	// 강한 좀비는 살짝 붉게
	override val color = Utils.rgb(255, 204, 204);

	override fun update(delta: Float) {
		val target: LivingEntity? = this.target;
		if(target == null) return;

		when(dashState) {
			DashState.WALKING -> {
				val distance = distanceTo(target);
				if(distance < 250f) {
					dashState = DashState.PREPARING;
					stateTimer = 0.5f;

					// 대기 상태에 들어가는 첫 프레임. 이때 플레이어를 조준해서 방향을 기억해둔다
					val dx = target.x - x;
					val dy = target.y - y;
					if(distance > 0) {
						dashDirX = dx / distance;
						dashDirY = dy / distance;
					}
				}
			}
			DashState.PREPARING -> {
				setMovable(false);
				stateTimer -= delta;
				if(stateTimer <= 0f) {
					dashState = DashState.DASHING;
					stateTimer = 0.4f;
				}
			}
			DashState.DASHING -> {
				// super 업데이트 로직 때 따로 안 움직이게 함
				setMovable(false);

				x += dashDirX * 900f * delta;
				y += dashDirY * 900f * delta;

				// 돌진 중에 플레이어랑 부딪히면 대미지 주고 즉시 쿨타임으로 넘어감
				if(collidesWith(target)) {
					target.takeDamage(20, attacker = this);
					dashState = DashState.COOLDOWN;
					setMovable(true);
					stateTimer = 5.0f;
				} else {
					stateTimer -= delta;

					if(stateTimer <= 0f) {
						dashState = DashState.COOLDOWN;
						setMovable(true);
						stateTimer = 5.0f;
					}
				}
			}
			DashState.COOLDOWN -> {
				stateTimer -= delta;
				if(stateTimer <= 0f)
					dashState = DashState.WALKING;
			}
		}

		// 세팅이 끝난 후 마지막에 부모를 호출해서 이동
		super.update(delta);
	}

	/**
	 * 평상시, 돌진하려고 잠깐 멈춰있음, 돌진, 돌진 쿨
	 */
	private enum class DashState {
		WALKING,
		PREPARING,
		DASHING,
		COOLDOWN;
	}
}
