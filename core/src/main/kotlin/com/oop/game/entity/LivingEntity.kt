package com.oop.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.oop.game.world.World;

/**
 * 살아있다는 개념이 있는 개체
 *
 * @param initialHp	초기(최대) 체력
 */
abstract class LivingEntity(world: World, x: Float, y: Float, width: Float, height: Float, texture: String, initialHp: Int) : Entity(world, x, y, width, height, texture) {
	open val maxHp: Int = initialHp;
	var hp = initialHp
		private set(value) {
			if(value > maxHp) field = maxHp;
			else if(value < 0) field = 0;
			else field = value;
		};
	val isAlive: Boolean
		get() = hp > 0;
	// 피격 시 잠깐 동안 대미지를 안 받게 해주는 무적 타이머.
	private var invincibilityTimer: Float = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	// 무적 타이머가 가동 중인지의 여부
	val isInvincible: Boolean
		get() = (invincibilityTimer > 0f);
	// 가장 최근 대미지를 입힌 개체
	var latestAttacker: Entity? = null
		private set;
	// 대미지를 입으면 0.5초 동안 붉게 표시할 때 사용되는 타이머
	private var damagedIndicatorTimer: Float = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	protected open val showDamagedIndicator = true;
	protected open val damagedIndicatorDuration = 0.5f;
	open val defaultInvincibleDuration = 0.0f;
	
	/**
	 * 체력 감소(대미지를 입는다.)
	 *
	 * @param damage				피해량
	 * @param invincibleDuration	무적 타이머
	 * @param attacker				공격자
	 */
	open fun takeDamage(damage: Int, invincibleDuration: Float = defaultInvincibleDuration, attacker: Entity? = null) {
		if(damage < 0) throw IllegalArgumentException("damage must not be negative");
		
		// 무적 시간이 다 끝났을 때만 피격당함
		if(!isInvincible) {
			hp -= damage;
			if(hp == 0) {  // 사망
				onDeath(attacker);  // 콜백 호출
				if(attacker != null) attacker.onKill(this);
			}
			invincibilityTimer = invincibleDuration;  // 한 대 맞았으니 지정된 시간만큼 무적 켤게!
			onDamage(damage, attacker);
			if(attacker != null) {
				attacker.onAttack(this);
				latestAttacker = attacker;
			}
			
			// 타격 시 붉게 표시 타이머
			if(showDamagedIndicator)
				damagedIndicatorTimer = damagedIndicatorDuration;
		}
	}
	
	/**
	 * 체력을 회복한다.
	 *
	 * @param amount	회복할 양
	 */
	open fun heal(amount: Int) {
		hp += amount;
	}
	
	/**
	 * 개체를 죽인다.
	 */
	fun kill(attacker: Entity? = null) {
		hp = 0;
		onDeath(attacker);
		if(attacker != null) attacker.onKill(this);
	}
	
	/**
	 * 대미지를 받았을 때 실행할 콜백 함수
	 *
	 * @param damage	받은 피해량
	 * @param attacker	공격자
	 */
	open fun onDamage(damage: Int, attacker: Entity?) {}
	
	/**
	 * 죽었을 때 실행할 콜백 함수
	 *
	 * @param killer	공격자
	 */
	open fun onDeath(killer: Entity?) {}

	/**
	 * 매 프레임 무적 시간 감소
	 */
	override fun update(delta: Float) {
		super.update(delta);
		
		if(invincibilityTimer > 0f)
			invincibilityTimer -= delta;
		if(damagedIndicatorTimer > 0f)
			damagedIndicatorTimer -= delta;
		
		// 몸 대미지 처리
		for(entity in world.getEntities())
			if(entity !== this && collidesWith(entity) && distanceTo(entity) < 8f && entity.bodyDamage > 0 && (!ignoreFriendBodyDamage || (ignoreFriendBodyDamage && this::class != entity::class))) {
				val attacker = if(entity is Bullet) entity.shooter else entity;  // 일단 총알은 Bullet 클래스에서 자체적으로 처리하고 bodyDamage는 0이기 때문에 의미는 없지만...
				takeDamage(entity.bodyDamage, attacker=attacker);
			}
	}
	
	override fun draw(batch: SpriteBatch) {
		val showDamaged = (showDamagedIndicator && damagedIndicatorTimer > 0f);
		if(showDamaged) batch.color = Color.RED;
		super.draw(batch);
		if(showDamaged) batch.color = Color.WHITE;
	}
}
