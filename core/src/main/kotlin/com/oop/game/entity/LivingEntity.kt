package com.oop.game.entity;

import com.oop.game.world.World;

/**
 * 살아있다는 개념이 있는 개체
 *
 * @param initialHp	초기(최대) 체력
 */
abstract class LivingEntity(world: World, x: Float, y: Float, width: Float, height: Float, texture: String, initialHp: Int = 100) : Entity(world, x, y, width, height, texture) {
	// 최대hp,  initialhp: 객체 만들 떄 지정할 체력
	open val maxHp: Int = initialHp
	// HP
	var hp: Int = initialHp
		private set(value) {
			if(value > maxHp) field = maxHp
			else if(value < 0) field = 0
			else field = value
		}
	// 피격 시 잠깐 동안 데미지를 안 받게 해주는 무적 타이머. 자식들도 알 수 있게 protected로 설정.
	protected var invincibilityTimer: Float = 0f
		private set(value) {
			if(value < 0.0f) field = 0.0f;
			else field = value;
		};

	open fun takeDamage(damage: Int, duration: Float = 0f) {
		// 무적 시간이 다 끝났을 때만 피격당함
		if (invincibilityTimer == 0f) {
			if(damage > 0) hp -= damage
			invincibilityTimer = duration  // 한 대 맞았으니 지정된 시간만큼 무적 켤게!
			onDamage();
		}
	}
	
	open fun onDamage() {}
	
	open fun heal(amount: Int) {
		hp += amount;
	}

	// 매프레임 무적 시간 감소 로직
	override fun update(delta: Float) {
		if (invincibilityTimer > 0f) {
			invincibilityTimer -= delta
		}
	}

	/**
	 * 이 객체가 아직 '살아있는지' 여부.
	 *
	 * GameWorld 가 매 프레임 removeDead() 를 호출하면,
	 *   이 값이 false 인 객체가 월드에서 정리된다.
	 *
	 * 기본값은 true — 대부분의 객체는 '살아있는 게 기본' 이기 때문.
	 * 'open' 이므로 서브클래스에서 원한다면 override 할 수 있다.
	 *   예) class Bullet(val worldHeight: Float) {
	 *           override fun isAlive() = y in 0f..worldHeight   // 화면 안에 있을 때만 살아있음
	 *       }
	 */
	open fun isAlive(): Boolean = hp > 0;
}
