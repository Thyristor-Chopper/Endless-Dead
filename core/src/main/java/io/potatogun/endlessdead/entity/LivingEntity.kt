package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

/**
 * 살아있다는 개념과 피격이 있는 개체
 *
 * @param world         개체가 속한 세계
 * @param x             개체의 처음 X 위치
 * @param y             개체의 처음 Y 위치
 * @param width         가로 크기 (픽셀)
 * @param height        세로 크기 (픽셀)
 * @param texture       개체 텍스처(없을 수도 있음)
 * @param initialHealth 초기(최대) 체력
 */
abstract class LivingEntity(world: World, x: Float, y: Float, width: Float, height: Float, texture: Texture? = null, initialHealth: Int) : Entity(world, x, y, width, height, texture) {
	/**
	 * 개체의 최대 체력.
	 */
	open val maxHealth: Int = initialHealth;
	/**
	 * 개체의 현재 체력
	 */
	var health = initialHealth
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
	 * 피격 시 잠깐 동안 대미지를 안 받게 해주는 무적 타이머
	 */
	private var invincibilityTimer: Float = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	/** 
	 * 무적 타이머가 가동 중인지의 여부
	 */
	private val isInvincibilityTimerActive: Boolean
		inline get() = (invincibilityTimer > 0f);
	/**
	 * 가장 최근에 대미지를 입힌 개체
	 */
	var latestAttacker: Entity? = null
		private set;
	/**
	 * 대미지를 입으면 0.5초 동안 붉게 표시할 때 사용되는 타이머
	 */
	private var damagedIndicatorTimer: Float = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	/**
	 * 대미지를 입었을 때 붉게 표시할지의 여부
	 */
	protected open val showDamageIndicator = true;
	/**
	 * 대미지를 입었을 때 붉게 표시되는 기간
	 */
	protected open val damageIndicatorDuration = 0.5f;
	/**
	 * 기본값 무적 타이머는 없음
	 */
	protected open val defaultInvincibleDuration = 0f;

	/**
	 * 체력 감소(대미지를 입는다.)
	 *
	 * @param damage             피해량
	 * @param invincibleDuration 무적 타이머
	 * @param attacker           공격자
	 * @return                   성공 여부
	 * @throws IllegalArgumentException	피해량이 잘못된 경우
	 */
	@JvmOverloads open fun takeDamage(damage: Int, invincibleDuration: Float = defaultInvincibleDuration, attacker: Entity? = null): Boolean {
		if(damage < 0) throw IllegalArgumentException("damage must not be negative");
		if(!isAlive) return false;

		// 무적 시간이 다 끝났을 때만 피격당함
		if(isInvincibilityTimerActive) return false;

		health -= damage;
		val killed = (health == 0);
		if(killed) {  // 사망
			onDeath(attacker);  // 콜백 호출
			if(attacker != null) attacker.onKill(this);
			remove();
		} else {
			invincibilityTimer = invincibleDuration;  // 한 대 맞았으니 지정된 시간만큼 무적 켤게!
			onDamage(damage, attacker);
			// 타격 시 붉게 표시 타이머
			if(showDamageIndicator)
				damagedIndicatorTimer = damageIndicatorDuration;
		}
		if(attacker != null) {
			if(!killed) attacker.onAttack(this);
			latestAttacker = attacker;
		}
		return true;
	}

	/**
	 * 체력을 회복한다.
	 *
	 * @param amount 회복할 양
	 * @return       성공 여부
	 */
	open fun heal(amount: Int): Boolean {
		if(!isAlive) return false;
		health += amount;
		return true;
	}

	/**
	 * 개체를 즉시 죽인다.
	 *
	 * @param attacker 공격자
	 * @return         성공 여부
	 */
	@JvmOverloads fun kill(attacker: Entity? = null): Boolean {
		if(!isAlive) return false;
		health = 0;
		onDeath(attacker);
		if(attacker != null) attacker.onKill(this);
		remove();
		return true;
	}

	/**
	 * 대미지를 받았을 때 실행할 콜백 함수
	 *
	 * @param damage   받은 피해량
	 * @param attacker 공격자
	 */
	open fun onDamage(damage: Int, attacker: Entity?) {}

	/**
	 * 죽었을 때 실행할 콜백 함수
	 *
	 * @param killer 공격자
	 */
	open fun onDeath(killer: Entity?) {}

	/**
	 * 매 프레임 무적 시간 감소
	 */
	override fun forceUpdate(delta: Float) {
		super.forceUpdate(delta);

		if(invincibilityTimer > 0f)
			invincibilityTimer -= delta;
		if(damagedIndicatorTimer > 0f)
			damagedIndicatorTimer -= delta;

		// 몸 대미지 처리
		for(entity in world.entities.getNearby(this)) {
			if(entity is BodyDamagable && entity !== this && !isSameTeamWith(entity) && collidesWith(entity)) {
				val attacker = if(entity is Bullet) entity.shooter else entity;  // 일단 총알은 Bullet 클래스에서 자체적으로 처리하고 bodyDamage는 0이기 때문에 의미는 없지만...
				takeDamage(entity.bodyDamage, attacker=attacker);
			}
		}
	}

	/**
	 * 대미지를 입은 경우 붉게 바꾼다.
	 */
	override fun draw(batch: SpriteBatch, alternateTexture: Texture?) {
		val showDamaged = (showDamageIndicator && damagedIndicatorTimer > 0f);
		if(showDamaged) batch.color = Color.RED;
		super.draw(batch, alternateTexture);
		if(showDamaged) batch.color = Color.WHITE;
	}
}
