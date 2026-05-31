package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.world.World;

import kotlin.math.sqrt;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  좀비 — zombie.bmp 이미지, 플레이어를 따라 자동 이동.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  Entity를 상속해 만든 '입력 없이 스스로 움직이는' 객체 예제.
 *
 *  핵심 포인트:
 *   ▸ update()에 입력 처리가 없다 — AI(자율 행동)는 여기서 작성.
 *
 *  응용 아이디어:
 *   ▸ 생성자에서 speed를 받아 FastEnemy, SlowEnemy로 다양화
 *   ▸ 이동 패턴을 사인파, 원운동 등으로 바꾸기
 *
 * @param world			개체가 속한 세계
 * @param position		개체의 처음 위치
 * @param width			가로 크기 (픽셀)
 * @param height		세로 크기 (픽셀)
 * @param hp			최대 체력
 * @param attackDamage	공격력
 * @param speed			이동 속도
 * @param texture		개체 텍스처
 */
open class Zombie(world: World, position: Position, width: Float, height: Float, hp: Int, attackDamage: Int, protected var speed: Float = 100f, texture: String = "zombie.bmp") : LivingEntity(world, position, width, height, texture, hp) {
    var attackDamage = attackDamage
		protected set;
	override val penetrationDamage = 1;
	override val defaultInvincibleDuration = 0.25f;
	val target: LivingEntity = world.player;
    // 💡 자식들이 언제든 거리를 실시간으로 잴 수 있게 열어둔 공용 프로퍼티
    protected val distanceToTarget: Float
        inline get() = distanceTo(target);

	override fun update(delta: Float) {
		super.update(delta);

        val dx = target.position.x - position.x;
        val dy = target.position.y - position.y;
        val distance = distanceToTarget;

		// 플레이어의 중심으로 정확히 모이면 어색하니까 살짝은 거리를 두게 하자.
        if(distance > target.width * (3f / 4f)) {
            position.x += dx / distance * speed * delta;
            position.y += dy / distance * speed * delta;
        } else {
			target.takeDamage(attackDamage, attacker = this);
		}
    }

	class Weak(world: World, position: Position) : Zombie(world, position, width=21f, height=30f, hp=3, speed=150f, attackDamage=1);

	class Normal(world: World, position: Position) : Zombie(world, position, width=32f, height=45f, hp=5, speed=100f, attackDamage=3);

	class Strong(world: World, position: Position) : Zombie(world, position, width=49f, height=70f, hp=15, speed=50f, attackDamage=5) {
        // 평상시 스피드, 대미지
        val originalSpeed = speed;
        val originalDamage = attackDamage;
        private var dashState = DashState.WALKING;
        private var stateTimer = 0f;
        //  돌진할 '방향(벡터)'을 기억해 둘 변수
        private var dashDirX = 0f;
        private var dashDirY = 0f;

        override fun update(delta: Float) {
            when(dashState) {
                DashState.WALKING -> {
                    val dist = distanceToTarget;
                    if(dist < 250f) {
                        dashState = DashState.PREPARING;
                        stateTimer = 0.5f;

                        // 대기 상태에 들어가는 첫 프레임, 이때 플레이어를 조준해서 방향을 기억해둔다
                        val dx = target.position.x - position.x;
                        val dy = target.position.y - position.y;
                        if(dist > 0) {
                            dashDirX = dx / dist;
                            dashDirY = dy / dist;
                        }
                    }
                }
                DashState.PREPARING -> {
                    speed = 0f;
                    stateTimer -= delta;
                    if(stateTimer <= 0f) {
                        dashState = DashState.DASHING;
                        stateTimer = 0.4f;
                    }
                }
                DashState.DASHING -> {
                    // speed 0으로 해서 super 업데이트 로직 때 따로 안 움직이게 함
                    speed = 0f;
                    attackDamage = 20;

                    position.x += dashDirX * 800f * delta;
                    position.y += dashDirY * 800f * delta;

                    // 돌진 중에 플레이어랑 부딪히면, 대미지 주고 즉시 쿨타임으로 넘어감
                    if(collidesWith(target)) {
                        target.takeDamage(attackDamage, attacker = this);
                        dashState = DashState.COOLDOWN;
                        speed = originalSpeed;
                        attackDamage = originalDamage;
                        stateTimer = 5.0f;
                    } else {
                        stateTimer -= delta;

                        if(stateTimer <= 0f) {
                            dashState = DashState.COOLDOWN;
                            speed = originalSpeed;
                            attackDamage = originalDamage;
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

            // 💡 속도 세팅이 완벽히 끝난 후, 마지막에 부모를 호출해서 이동시킴
            super.update(delta);
        }

		// 평상시, 돌진하려고 잠깐 멈춰있음, 돌진, 돌진 쿨
        private enum class DashState {
            WALKING,
			PREPARING,
			DASHING,
			COOLDOWN;
        }
    }
}
