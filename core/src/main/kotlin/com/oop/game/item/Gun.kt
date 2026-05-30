package com.oop.game.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import com.oop.game.Position;
import com.oop.game.Timer;
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
abstract class Gun(world: World, id: String, name: String, override val bulletDamage: Int, override val bulletSpeed: Float, override val bulletHp: Int, override val penetrable: Boolean, val fireInterval: Float, val maxAmmo: Int, initialAmmo: Int) : Item(world, id, name), Fireable, Usable {
	override val allowContinuousUse = false;
	private var fireCooldown = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	val canFire: Boolean
		get() = fireCooldown == 0f && ammo > 0;
	var ammo: Int = initialAmmo
		protected set(value) {
			if(value < 0) field = 0;
			else if(value > maxAmmo) field = maxAmmo;
			else field = value;
		} //샷건이라는 하위클래스에서도 사용해야할 것 같아 private를 protected로 변경
	private var cooldownTimer: Timer? = null;
	
	/**
	 * 총의 쿨타임을 건다.
	 */
	protected fun startFireCooldown() {
		fireCooldown = fireInterval;
		
		// 남은 쿨타임을 갱신한다.
		cooldownTimer = Timer(0.01f) {
			fireCooldown -= 0.01f;
			if(fireCooldown == 0f)
				cooldownTimer?.unregister();
		}.register();
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
	 * @param	target	총알이 향할 좌표
	 * @param	shooter	총알을 쏜 개체
	 * @return	발사 성공 여부
	 */
	override fun fire(target: Position, shooter: Entity): Boolean {
		if(!canFire) return false;
		
		val bullet = Bullet(world, this, shooter, target, bulletSpeed, bulletDamage, penetrable, bulletHp);
		world.addEntity(bullet);
		startFireCooldown();
		ammo--;
		
		// ammo가 다 떨어진 총은 파괴
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
		return fire(Position(Gdx.input.getX().toFloat() + world.offsetX, world.game.screenHeight - Gdx.input.getY().toFloat() + world.offsetY), world.player);
	}
	
	override fun cleanUp() {
		cooldownTimer?.unregister();
	}
}
