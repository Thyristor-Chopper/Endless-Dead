package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.gdxhelper.util.getDistanceSorted;
import io.potatogun.gdxhelper.world.World;

class FriendlyTurret(world: World, x: Float, y: Float) : Turret(world, x, y, Textures.getShared("turret_friendly"), Gun.Properties(1, 700f).fireInterval(0.6f)) {
	init {
		setTargetFetcher { world.entities.getDistanceSorted(this).firstOrNull { it is Zombie } as? Zombie };
		team = "friends";
	}

	// 공유 자원이라 정리 안 함
	override fun dispose() {}
}
