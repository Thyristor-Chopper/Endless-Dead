package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.getRandom;
import io.potatogun.gdxhelper.world.World;

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
 * @param    world         개체가 속한 세계
 * @param    x             개체의 처음 X 위치
 * @param    y             개체의 처음 Y 위치
 * @param    width         가로 크기 (픽셀)
 * @param    height        세로 크기 (픽셀)
 * @param    hp            최대 체력
 * @property attackDamage  공격력
 * @property speed         이동 속도
 * @param    texture       개체 텍스처
 * @param    initialTarget 처음 공격 대상
 */
open class Zombie(world: World, x: Float, y: Float, width: Float, height: Float, hp: Int, protected open val attackDamage: Int, protected open val speed: Float, texture: Texture = Textures.getShared("zombie"), initialTarget: LivingEntity? = null) : LivingEntity(world, x, y, width, height, texture, hp), PenetratorDamagable {
	protected open val attackingTexture = Textures.getShared("attacking_zombie");
	override val penetrationDamage = 1;
	override val defaultInvincibleDuration = 0.25f;
	var target: LivingEntity? = initialTarget  // 자바에서도 getTarget()같은 거 많아서 어느 정도의 캡슐화는 유지하기 위해 @JvmField 없음
		private set;
	protected open val attackInterval = 0.3f;
	private var attackTextureTimer = 0f;
	private var attackCooldownTimer = attackInterval;

	/**
	 * 공격 대상을 가져오거나 대상이 사라지면 초기화한다.
	 *   기본 공격 대상 선정 방식을 바꿀 수도 있으므로 open이다.
	 *
	 * @return 공격 대상
	 */
	protected open fun getTargetOrReset(): LivingEntity? {
		val target: LivingEntity? = this.target;
		if(target == null || !target.isAlive) {
			val world = this.world;
			this.target = if(world is SinglePlayerWorld) world.player else world.entities.getRandom<Player>();
			return null;  // 다음 프레임에...
		}
		return target;
	}

	override fun update(delta: Float) {
		super.update(delta);

		val target: LivingEntity? = getTargetOrReset();
		if(target == null) return;

		val dx = target.x - x;
		val dy = target.y - y;
		val distance = distanceTo(target);

		// 플레이어의 중심으로 정확히 모이면 어색하니까 살짝은 거리를 두게 하자.
		if(distance > target.width * (3f / 4f)) {
			attackTextureTimer = 0f;
			attackCooldownTimer = 0f;
			x += dx / distance * speed * delta;
			y += dy / distance * speed * delta;
		} else {
			// 플레이어에게 대미지 주기
			// 처음 템플릿(예제) 코드에서는 모든 상호작용을 월드에서 처리했으나
			//   난 좀비'가' 누군가에게 직접 대미지를 주는 게 맞는 것 같아서 여기서 처리함.
			attackTextureTimer -= delta;
			if(attackTextureTimer <= 0f)
				attackTextureTimer = 0.75f;
			attackCooldownTimer -= delta;
			if(attackCooldownTimer <= 0f)
				attackCooldownTimer = attackInterval;

			if(attackCooldownTimer == attackInterval)
				target.takeDamage(attackDamage, attacker = this);
		}
	}

	override fun draw(batch: SpriteBatch) {
		if(attackTextureTimer % 0.75f > 0.5f)
			super.draw(batch, attackingTexture);
		else
			super.draw(batch);
	}

	// 누군가가 자신을 공격하면 처치 대상을 그자로 한다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			target = attacker;
	}

	// 공유 자원이기 때문에 여기서 정리하지 않고 다른 인스턴스에서 재활용한다.
	override fun dispose() {}

	class Weak(world: World, x: Float, y: Float, initialTarget: LivingEntity? = null) : Zombie(world, x, y, width=21f, height=30f, hp=3, speed=150f, attackDamage=1, initialTarget = initialTarget);

	class Normal(world: World, x: Float, y: Float, initialTarget: LivingEntity? = null) : Zombie(world, x, y, width=32f, height=45f, hp=5, speed=100f, attackDamage=3, initialTarget = initialTarget);

	class Strong(world: World, x: Float, y: Float, initialTarget: LivingEntity? = null) : Zombie(world, x, y, width=49f, height=70f, hp=15, speed=50f, attackDamage=5, initialTarget = initialTarget) {
		// 평상시 스피드, 대미지
		private val originalSpeed = super.speed;
		private val originalDamage = super.attackDamage;
		override var speed = originalSpeed;
		override var attackDamage = originalDamage;
		private var dashState = DashState.WALKING;
		private var stateTimer = 0f;
		// 돌진할 '방향(벡터)'을 기억해 둘 변수
		private var dashDirX = 0f;
		private var dashDirY = 0f;
		// 강한 좀비는 살짝 붉게
		override val color = Utils.rgb(255, 192, 192);

		override fun update(delta: Float) {
			val target: LivingEntity? = getTargetOrReset();
			if(target == null) return;

			when(dashState) {
				DashState.WALKING -> {
					val distance = distanceTo(target);
					if(distance < 250f) {
						dashState = DashState.PREPARING;
						stateTimer = 0.5f;

						// 대기 상태에 들어가는 첫 프레임, 이때 플레이어를 조준해서 방향을 기억해둔다
						val dx = target.x - x;
						val dy = target.y - y;
						if(distance > 0) {
							dashDirX = dx / distance;
							dashDirY = dy / distance;
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

					x += dashDirX * 800f * delta;
					y += dashDirY * 800f * delta;

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

			// 속도 세팅이 완벽히 끝난 후, 마지막에 부모를 호출해서 이동시킴
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
}
