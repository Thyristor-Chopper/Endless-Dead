package com.oop.game.item;

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
 * @param speed			총알 속도
 * @param fireInterval	공격 속도
 */
abstract class Gun(world: World, id: String, name: String, damage: Int, speed: Float, penetreble: Boolean, fireCooldownInterval: Float, private val maxAmmo: Int, var ammo: Int) : Item(world, id, name), Fireable {
	override val bulletDamage = damage
	override val bulletSpeed = speed
	override val bulletPenetrable = penetreble;
	override val fireInterval = fireCooldownInterval;
	private var fireCooldown = 0f
		set(value) {
			if(value < 0.0f) field = 0.0f;
			else field = value;
		}
	val canShoot: Boolean
		get() = fireCooldown == 0f;

	override fun update(delta: Float) {
		if(fireCooldown > 0f)
			fireCooldown -= delta
	}
	
	private fun startFireCooldown() {
		fireCooldown = fireInterval
	}
	
	override fun fire(target: Position, shooter: Entity): Boolean {
		if(!canShoot) return false;
		
		val bullet = Bullet(world, shooter.x, shooter.y, target, bulletSpeed, bulletDamage, bulletPenetrable);
		world.add(bullet);
		startFireCooldown();
		return true;
	}
}//maxAmmo와 ammo 프로퍼티도 필요할 듯? fireinterval, fire 함수도
