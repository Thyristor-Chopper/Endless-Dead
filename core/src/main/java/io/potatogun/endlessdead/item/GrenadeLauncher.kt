package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.Grenade;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;

/**
 * 유탄 발사기
 */
class GrenadeLauncher : Gun("grenade_launcher", "Grenade Launcher", Gun.Properties(15, 200f).fireInterval(3f).bullets(20)) {
	override fun createBullets(target: Position, shooter: Entity): Int {
		val world = shooter.world;
		if(world == null) return 0;
		val bullet = Grenade(world, shooter, target, bulletSpeed, bulletDamage);
		world.entities.add(bullet);
		if(!infiniteBullets)
			bullets--;
		return 1;
	}
}
