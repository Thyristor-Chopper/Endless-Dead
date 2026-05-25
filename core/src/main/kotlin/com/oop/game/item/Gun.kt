package com.oop.game.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import com.oop.game.Position;
import com.oop.game.entity.Bullet;
import com.oop.game.entity.Entity;
import com.oop.game.world.World;

/**
 * 총 아이템 추상 클래스
 *
 * @param world			아이템이 있는 세계
 * @param id			총 식별자
 * @param name			총 이름
 * @param damage		총알 피해량
 * @param bulletSpeed	총알 속도
 * @param penetrable	총알 관통 가능 여부
 * @param fireInterval	공격 속도
 * @param maxAmmo		최대 총알 개수
 * @param initialAmmo	초기 총알 개수
 */
abstract class Gun(world: World, id: String, name: String, override val bulletDamage: Int, override val bulletSpeed: Float, override val penetrable: Boolean, val fireInterval: Float, val maxAmmo: Int, initialAmmo: Int) : Item(world, id, name), Fireable, Usable {
	override val allowContinuousUse = false;
	private var fireCooldown = 0f
		set(value) {
			if(value < 0.0f) field = 0.0f;
			else field = value;
		};
	val canFire: Boolean
		get() = fireCooldown == 0f && ammo > 0;
	var ammo: Int = initialAmmo
		private set(value) {
			if(value < 0) field = 0;
			else if(value > maxAmmo) field = maxAmmo;
			else field = value;
		};
	
	/**
	 * 남은 쿨타임을 갱신한다.
	 */
	override fun update(delta: Float) {
		if(fireCooldown > 0f)
			fireCooldown -= delta;
	}
	
	/**
	 * 총을 쏘고 쿨타임을 건다.
	 */
	private fun startFireCooldown() {
		fireCooldown = fireInterval;
	}
	
	/**
	 * 남은 쿨타임을 전체 공격 간격에 비례하여 0~1로 정규화하여 반환한다.
	 */
	fun getRemainingCooldownPercentage(): Float {
		return fireCooldown / fireInterval;
	}
	
	/**
	 * 총 쏘기
	 *
	 * @return 발사 성공 여부
	 */
	override fun fire(target: Position, shooter: Entity): Boolean {
		if(!canFire) return false;
		
		val bullet = Bullet(world, shooter, target, bulletSpeed, bulletDamage, penetrable);
		world.add(bullet);
		startFireCooldown();
		ammo--;
		
		// ammo가 다 떨어진 총은 파괴 (만약 충전 기능을 만든다면 이 코드는 비활성화할 수도 있음)
		if(ammo == 0) {
			if(holder === world.player)
				world.drawSubtitles("Gun destroyed; no more bullets left", color=Color.SALMON);
			destroy();
		}
		
		return true;
	}
	
	/**
	 * 아이템 사용 처리
	 *
	 * @return 사용 성공 여부
	 */
	override fun use(): Boolean {
		return fire(Position(Gdx.input.getX().toFloat() + world.offsetX, world.screenHeight - Gdx.input.getY().toFloat() + world.offsetY), world.player);
	}
}
//maxAmmo와 ammo 프로퍼티도 필요할 듯? fireinterval, fire 함수도
