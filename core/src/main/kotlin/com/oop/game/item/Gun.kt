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
 */
abstract class Gun(world: World, id: String, name: String, override val bulletDamage: Int, override val bulletSpeed: Float, override val penetrable: Boolean, override val fireInterval: Float, val maxAmmo: Int, initialAmmo: Int) : Item(world, id, name), Fireable, Usable {
	override val allowContinuousUse = true;
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

	override fun update(delta: Float) {
		if(fireCooldown > 0f)
			fireCooldown -= delta
	}
	
	private fun startFireCooldown() {
		fireCooldown = fireInterval
	}
	
	fun getRemainingCooldownPercentage(): Float {
		return fireCooldown / fireInterval;
	}
	
	override fun fire(target: Position, shooter: Entity): Boolean {
		if(!canFire) return false;
		
		val bullet = Bullet(world, shooter, target, bulletSpeed, bulletDamage, penetrable);
		world.add(bullet);
		startFireCooldown();
		ammo--;
		
		// ammo가 다 떨어진 총은 파괴 (만약 충전 기능을 만든다면 이 코드는 비활성화할 수도 있음)
		if(ammo == 0) {
			destroy();
			if(holder === world.player)
				world.drawSubtitles("Gun destroyed; no more bullets left", color=Color.SALMON);
		}
		
		return true;
	}
	
	override fun use(): Boolean {
		return fire(Position(Gdx.input.getX().toFloat() + world.offsetX, world.screenHeight - Gdx.input.getY().toFloat() + world.offsetY), world.player);
	}
}
//maxAmmo와 ammo 프로퍼티도 필요할 듯? fireinterval, fire 함수도
