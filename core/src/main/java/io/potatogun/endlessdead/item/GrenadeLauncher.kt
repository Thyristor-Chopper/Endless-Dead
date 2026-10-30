package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.Grenade;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;

/**
 * 유탄 발사기
 */
class GrenadeLauncher : Gun("grenade_launcher", "Grenade Launcher", Gun.Properties(15, 200f).fireInterval(3f).bullets(20).rarity(Rarity.UNCOMMON)) {
	private val shrapnelSpeed = 400f;
	private val shrapnelDamage = bulletDamage;

	override fun createBullets(target: Position, shooter: Entity): Int {
		val world = shooter.world;
		if(world == null) return 0;
		val grenade = Grenade(world, shooter, target, bulletSpeed, shrapnelSpeed, shrapnelDamage);
		world.entities.add(grenade);
		if(!infiniteBullets)
			bullets--;
		return 1;
	}
}
