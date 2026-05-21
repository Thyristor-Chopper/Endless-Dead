package com.oop.game

import com.oop.game.GameObject


abstract class Gun(
	world: GameWorld,
	id: String,
	name: String,
	damage: Int,
	speed: Float,
	val fireinterval : Float,
	private val maxAmmo : Int,
	var ammo : Int
) : Item(world, id, name), Shootable {
	override val bulletDamage = damage
	override val bulletSpeed = speed
	private var fireCooldown = 0f

	fun update(delta: Float) {
		if (fireCooldown > 0f) {
			fireCooldown -= delta
		}
	}
	override fun shoot(target: Position, shooter: LivingGameObject) {
		val bullet = Bullet(world, shooter.x, shooter.y, target, bulletSpeed, bulletDamage);
		world.add(bullet);
	}
}//maxAmmo와 ammo 프로퍼티도 필요할 듯? fireinterval, fire 함수도

class shotGun(world: GameWorld): Gun(
	world,
	"G002",
	"샷건",
	20,
	5f,
	2f,
	10,
	5
){
// 기능들 넣을까 예정
}
class machineGun(world: GameWorld) : Gun(
	world,
	"G003",
	"기관총",
	5,
	5f,
	0f,
	30,
	30
){

}
