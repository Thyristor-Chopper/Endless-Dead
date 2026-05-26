package com.oop.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.oop.game.world.World;

/**
 * 살아있다는 개념이 있는 개체
 *
 * @param initialHp	초기(최대) 체력
 */
abstract class LivingEntity(world: World, x: Float, y: Float, width: Float, height: Float, texture: String, initialHp: Int = 100) : Entity(world, x, y, width, height, texture) {
	// 최대hp,  initialhp: 객체 만들 떄 지정할 체력
	open val maxHp: Int = initialHp;
	// HP
	var hp = initialHp
		private set(value) {
			if(value > maxHp) field = maxHp;
			else if(value < 0) field = 0;
			else field = value;
		};
	val isAlive: Boolean
		get() = hp > 0;
	// 피격 시 잠깐 동안 데미지를 안 받게 해주는 무적 타이머. 자식들도 알 수 있게 protected로 설정.
	protected var invincibilityTimer: Float = 0f
		private set(value) {
			if(value < 0.0f) field = 0.0f;
			else field = value;
		};
	var latestAttacker: Entity? = null
		private set;
	private var damagedIndicatorTimer: Float = 0.0f
		set(value) {
			if(value < 0.0f) field = 0.0f;
			else field = value;
		};
	open val showDamagedIndicator = true;
	open val damagedIndicatorDuration = 0.5f;
	
	/**
	 * 체력 감소(대미지를 입는다.)
	 *
	 * @param damage	피해량
	 * @param duration	무적 타이머
	 * @param attacker	공격자
	 */
	open fun takeDamage(damage: Int, duration: Float = 0f, attacker: Entity? = null) {
		// 무적 시간이 다 끝났을 때만 피격당함
		if (invincibilityTimer == 0f) {
			if(damage > 0) hp -= damage;
			if(hp == 0) {
				onDeath(attacker);
				if(attacker != null)
					attacker.onKill(this);
			}
			invincibilityTimer = duration;  // 한 대 맞았으니 지정된 시간만큼 무적 켤게!
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
	}
	
	override fun draw(batch: SpriteBatch) {
		val showDamaged = damagedIndicatorTimer > 0.0f;
		if(showDamaged) batch.color = Color.RED;
		super.draw(batch);
		if(showDamaged) batch.color = Color.WHITE;
	}
}
