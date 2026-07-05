package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.entity.listener.AttackListener;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.gdxhelper.entity.Entity;
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
 * @property health  최대 체력
 * @param    texture 개체 텍스처(없을 수도 있음)
 */
abstract class LivingEntity(world: World, name: String, x: Float, y: Float, width: Float, height: Float, health: Int, texture: Texture?) : Entity(world, name, x, y, width, height, texture), TeamMember {
	/**
	 * 개체의 최대 체력
	 */
	open val maxHealth: Int = health;
	/**
	 * 개체의 현재 체력
	 */
	var health = health
		private set(value) {
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
	 * 기본 이동 속도
	 */
	abstract val movementSpeed: Float;
	/**
	 * 손공격 피해량
	 */
	open val attackDamage: Int = 0;
	/**
	 * 손공격 간격 (낮을수록 빠름)
	 */
	open val attackInterval: Float = 0f;
	private var attackCooldownTimer = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
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
	 * 기본 무적 타이머 길이 (isInvincible과는 별개)
	 */
	protected open val damageInvincibilityDuration = 0f;
	/**
	 * 대미지를 입으면 0.5초 동안 붉게 표시할 때 사용되는 타이머
	 */
	private var damagedIndicatorTimer = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	/**
	 * 피격 시 잠깐 동안 대미지를 안 받게 해주는 무적 타이머 (isInvincible과는 별개)
	 */
	private var invincibilityTimer = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	/** 
	 * 피격 무적 타이머가 가동 중인지의 여부 (isInvincible과는 별개)
	 */
	private val isInvincibilityTimerActive: Boolean
		inline get() = (invincibilityTimer > 0f);
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
	 * @throws IllegalArgumentException	피해량이 잘못된 경우
	 */
	@JvmOverloads fun takeDamage(damage: Int, attacker: Entity? = null): Boolean {
		if(damage < 0) throw IllegalArgumentException("damage must not be negative");
		if(attacker != null && isSameTeamWith(attacker)) return false;
		if(isInvincible) return false;
		if(!isAlive) return false;

		// 무적 시간이 다 끝났을 때만 피격당함
		if(isInvincibilityTimerActive) return false;

		health -= damage;
		val killed = (health == 0);
		if(killed) {  // 사망
			_onDeath();
			if(this is DamageListener) onDeath(attacker);  // 콜백 호출
			if(attacker is AttackListener) attacker.onKill(this);
			remove();
		} else {
			invincibilityTimer = damageInvincibilityDuration;
			if(this is DamageListener) onDamage(damage, attacker);
			// 타격 시 붉게 표시 타이머
			if(showDamageIndicator)
				damagedIndicatorTimer = damageIndicatorDuration;
		}
		if(attacker != null) {
			if(!killed && attacker is AttackListener)
				attacker.onAttack(this);
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

	/**
	 * 죽었을 때의 필수 처리사항을 처리한다.
	 */
	private inline fun _onDeath() {  // 현재 한 곳에서만 쓰여서 인라인이며 두 곳 이상에서 쓰이게 되면 인라인 해제
		if(this is InventoryHolder && this is ItemDroppable)
			inventory.forEachItemsReverse { dropItem(it) };
	}

	/**
	 * 공격 대상에게 대미지를 입힌다.
	 *
	 * @param target 대상
	 * @return 성공 여부
	 */
	fun damageTarget(target: LivingEntity): Boolean {
		if(attackCooldownTimer > 0f) return false;
		val result = target.takeDamage(attackDamage, attacker = this);
		if(result) attackCooldownTimer = attackInterval;
		return result;
	}

	override fun update(delta: Float) {
		super.update(delta);

		if(invincibilityTimer > 0f)
			invincibilityTimer -= delta;
		if(attackCooldownTimer > 0f)
			attackCooldownTimer -= delta;
	}

	override fun forceUpdate(delta: Float) {
		super.forceUpdate(delta);

		if(damagedIndicatorTimer > 0f)
			damagedIndicatorTimer -= delta;

		// 몸 대미지 처리
		world.entities.forEachNearby(this) { entity ->
			if(entity is BodyDamagable && entity !== this && !isSameTeamWith(entity) && collidesWith(entity)) {
				takeDamage(entity.bodyDamage, attacker = entity);
			}
		};
	}

	// 대미지를 입은 경우 붉게 바꾼다.
	override fun draw(batch: SpriteBatch, textureOverride: Texture?, colorOverride: Color?) {
		val showDamaged = (colorOverride == null && showDamageIndicator && damagedIndicatorTimer > 0f);
		val color = if(showDamaged) Color.RED else colorOverride;
		super.draw(batch, textureOverride, color);
	}
}
