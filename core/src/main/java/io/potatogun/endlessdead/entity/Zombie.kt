package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.getDistanceSorted;
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
 * @param world    개체가 속한 세계
 * @param x        개체의 처음 X 위치
 * @param y        개체의 처음 Y 위치
 * @param width    가로 크기 (픽셀)
 * @param height   세로 크기 (픽셀)
 * @param settings 좀비 옵션
 */
abstract class Zombie(world: World, x: Float, y: Float, width: Float, height: Float, settings: Properties) : LivingEntity(world, "Zombie", x, y, width, height, settings.health, Textures.getShared("zombie")), PenetratorDamagable, AttackTargetable, DamageListener, MeleeAttacker, Movable {
	private val targeter = AutoTargeter(this, targetFetcher = { if(world is SinglePlayerWorld) world.player else world.entities.getDistanceSorted(this).firstOrNull { it is Player } as? Player });  // 클래스 정의 시 위임자에게 this만 넘길 수 있었어도 이딴 수동 위임같은 뻘짓 안 나오지...
	private val attackingTexture = Textures.getShared("attacking_zombie");
	override val attackDamage = settings.attackDamage;
	override val speed = settings.speed;
	override val penetrationDamage = 1;
	override val defaultInvincibleDuration = 0.15f;
	override val attackInterval = 0.3f;
	private var attackTextureTimer = 0f;
	private var attackCooldownTimer = attackInterval;
	override var target: LivingEntity?
		get() = targeter.target
		set(value) { targeter.target = value };
	private var isMovable = true;

	init {
		settings.fillDefaults();
	}

	protected fun setTargetFetcher(fetcher: () -> LivingEntity?) {
		targeter.setTargetFetcher(fetcher);
	}

	protected fun setFollowRange(range: Float) {
		targeter.setFollowRange(range);
	}

	protected fun setMovable(movable: Boolean) {
		isMovable = movable;
	}

	override fun update(delta: Float) {
		super.update(delta);

		val target: LivingEntity? = this.target;
		if(target == null) return;

		val dx = target.x - x;
		val dy = target.y - y;
		val distance = distanceTo(target);

		// 플레이어의 중심으로 정확히 모이면 어색하니까 살짝은 거리를 두게 하자.
		if(distance > target.width * (3f / 4f)) {
			attackTextureTimer = 0f;
			attackCooldownTimer = 0f;
			if(isMovable) {
				x += dx / distance * speed * delta;
				y += dy / distance * speed * delta;
			}
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
	//   자연 생성된 포탑은 공격 불가이기 때문에 그것에게 공격받아도 그걸 타겟하지는 않는다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			target = attacker;
	}

	/**
	 * 좀비 옵션
	 * 
	 * @property attackDamage 공격 피해량
	 * @property speed        이동 속도 (0이면 멈춤, 음수도 가능하지만 비권장)
	 * @throws IllegalArgumentException 값 일부가 잘못됐을 때
	 */
	open class Properties(@JvmField val health: Int, @JvmField val attackDamage: Int = 0, @JvmField val speed: Float = 0f) {
		init {
			if(attackDamage < 0) throw IllegalArgumentException("invalid attack damage");
			if(health <= 0) throw IllegalArgumentException("invalid health");
		}

		internal open fun fillDefaults() {}
	}
}
