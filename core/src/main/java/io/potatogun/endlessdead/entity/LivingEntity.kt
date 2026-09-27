package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.entity.forEachNearby;
import io.potatogun.endlessdead.entity.listener.AttackListener;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.max2;
import io.potatogun.gdxhelper.world.World;

/**
 * 살아있다는 개념과 피격, 행동이 있는 개체
 *
 * @param    world   개체가 속한 세계
 * @param    name    개체 표시 이름
 * @param    x       개체의 처음 X 위치
 * @param    y       개체의 처음 Y 위치
 * @param    width   가로 크기
 * @param    height  세로 크기
 * @property health  처음 체력
 * @param    texture 개체 텍스처(없을 수도 있음)
 */
abstract class LivingEntity @JvmOverloads constructor(world: World, name: String, x: Float, y: Float, width: Float, height: Float, health: Int, texture: Texture? = null) : Entity(world, name, x, y, width, height, texture), TeamMember {
	/**
	 * 개체의 최대 체력
	 */
	open val maxHealth: Int = health;
	/**
	 * 개체의 현재 체력
	 */
	var health = health
		protected set(value) {
			if(value > maxHealth) field = maxHealth;
			else if(value < 0) field = 0;
			else field = value;
		};
	/**
	 * 개체가 살아있는지의 여부
	 */
	val isAlive: Boolean
		get() = health > 0;
	/**
	 * 무적인지의 여부
	 */
	var isInvincible = false  // setter는 자바에서는 setInvincible임 디컴파일해서 확인함
		protected set;
	/**
	 * 대미지를 입었을 때 붉게 표시할지의 여부
	 */
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("canShowDamageIndicator")
	protected open val showDamageIndicator = true;
	/**
	 * 대미지를 입었을 때 붉게 표시되는 기간
	 */
	protected open val damageIndicatorDuration = 0.5f;
	/**
	 * 무적 타이머 길이 (isInvincible과는 별개)
	 */
	protected open val damageInvincibilityDuration = 0f;
	/**
	 * 대미지를 입으면 0.5초 동안 붉게 표시할 때 사용되는 타이머
	 */
	private var damagedIndicatorTimer = 0f;
	/**
	 * 피격 시 잠깐 동안 대미지를 안 받게 해주는 무적 타이머 (isInvincible과는 별개)
	 */
	private var invincibilityTimer = 0f;
	/** 
	 * 피격 무적 타이머가 가동 중인지의 여부 (isInvincible과는 별개)
	 */
	private val isInvincibilityTimerActive: Boolean
		inline get() = (invincibilityTimer > 0f);
	/**
	 * 피격 무적 타이머 활성 시간 동안 더 큰 대미지가 들어왔을 때를 감지하기 위한 변수
	 */
	private var accumulatedDamage = 0;
	/**
	 * 모든 살아있는 개체는 기본적으로 팀이 있으며 기본적으로 중립
	 */
	final override var team: String? = null;

	init {
		if(health <= 0) throw IllegalArgumentException("invalid health");
	}

	/**
	 * 체력 감소(대미지를 입는다.)
	 *
	 * @param damage   피해량
	 * @param attacker 공격자
	 * @return 성공 여부
	 * @throws IllegalArgumentException 피해량이 잘못된 경우
	 */
	@JvmOverloads fun takeDamage(damage: Int, attacker: Entity? = null): Boolean {
		if(damage < 0) throw IllegalArgumentException("damage must not be negative");
		if(attacker != null && isSameTeamWith(attacker)) return false;
		if(isInvincible) return false;
		if(!isAlive) return false;

		val finalDamage: Int;  // 실제로 받을 대미지 (아래에서 계산)
		// 무적 시간이 남아있을 경우 더 큰 대미지가 들어왔을 때만 해당 대미지 값으로 대체
		if(isInvincibilityTimerActive)
			finalDamage = max2(damage - accumulatedDamage, 0);
		else
			finalDamage = damage;

		// 피격 무적 시간 동안 새로 받은 공격의 대미지가 기존보다 작으면 처리 안 함
		if(finalDamage > 0)
			health -= finalDamage;

		// 피해 이벤트 발생 (무적 타이머가 켜져 있어서 실제 피해가 무시되어도 발생함.)
		if(this is DamageListener)
			onDamage(damage, attacker);  // 실제 피해량이 아닌 원 피해량임에 주의

		// 공격 이벤트 발생 (위와 동일)
		if(attacker is AttackListener)
			attacker.onAttack(this);

		// 실제 피해가 무시됐으면 이후 처리는 건너뜀.
		if(finalDamage <= 0)
			return false;

		if(health == 0) {  // 사망 시
			// 사망 이벤트 발생
			if(this is DamageListener)
				onDeath(attacker);

			// 죽임 이벤트 발생
			if(attacker is AttackListener)
				attacker.onKill(this);

			// 개체를 월드에서 제거
			remove();
		} else {
			// 무적 피격 타이머 활성화 (중복 활성화/갱신 방지)
			if(!isInvincibilityTimerActive && damageInvincibilityDuration > 0f) {
				invincibilityTimer = damageInvincibilityDuration;
				accumulatedDamage = 0;
			}

			// 타격 시 붉게 표시 타이머 활성화
			if(showDamageIndicator)
				damagedIndicatorTimer = damageIndicatorDuration;

			// 무적 시간 동안 대미지 누적
			if(isInvincibilityTimerActive)
				accumulatedDamage += finalDamage;
		}

		return true;
	}

	/**
	 * 체력을 회복한다.
	 *
	 * @param amount 회복할 양
	 * @return 성공 여부
	 */
	fun heal(amount: Int): Boolean {
		if(!isAlive) return false;
		health += amount;
		return true;
	}

	override fun forceUpdate(delta: Float) {
		super.forceUpdate(delta);

		if(invincibilityTimer > 0f) {
			invincibilityTimer -= delta;

			// 피격 무적 동안의 누적 대미지 초기화
			if(invincibilityTimer <= 0f)
				accumulatedDamage = 0;
		}

		if(damagedIndicatorTimer > 0f)
			damagedIndicatorTimer -= delta;

		// 몸 대미지 처리
		forEachNearby { entity ->
			if(entity is BodyDamagable && entity !== this && !isSameTeamWith(entity) && collidesWith(entity))
				takeDamage(entity.bodyDamage, attacker = entity);
		};
	}

	// 대미지를 입은 경우 붉게 바꾸는 고급 hook이다.
	override fun draw(batch: SpriteBatch, textureOverride: Texture?, tintOverride: Color?) {
		val showDamaged = (tintOverride == null && showDamageIndicator && damagedIndicatorTimer > 0f);
		val color = if(showDamaged) Color.RED else tintOverride;
		super.draw(batch, textureOverride, color);
	}
}
