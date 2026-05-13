package com.oop.game;

abstract class LivingGameObject(x: Float, y: Float, width: Float, height: Float,initialHp:Int=100) : GameObject(x, y, width, height) {
	 //HP
	//최대hp ,hp 캡슐화,initialhp:객체 만들떄 지정할 체력
	open  var maxHp: Int=initialHp

	var hp:Int=initialHp
		 private set(value) {
			 if(value>maxHp) field = maxHp
			 else if(value<0) field=0
			 else field=value
		 }
	fun hpSet(_hp:Int) {
		hp=_hp
	}
	//피격 시 잠깐 동안 데미지를 안 받게 해주는 무적 타이머. 자식들도 알 수 있게 protected로 설정.
	protected var invincibilityTimer: Float = 0f

	open fun takeDamage(damage: Int, duration: Float = 0f) {
		// 무적 시간이 다 끝났을 때만 피격당함
		if (invincibilityTimer <= 0f) {
			if(damage>0 )hp -= damage
			invincibilityTimer = duration // 한 대 맞았으니 지정된 시간만큼 무적 켤게!

		}
	}
	//매프레임 무적 시간 감소 로직
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
	open fun isAlive(): Boolean = hp>0;
}
