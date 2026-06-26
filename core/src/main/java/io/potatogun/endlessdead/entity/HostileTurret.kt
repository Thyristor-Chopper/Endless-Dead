package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.util.getDistanceSorted;
import io.potatogun.gdxhelper.world.World;

class HostileTurret(world: World, x: Float, y: Float) : Turret(world, x, y, Textures.getShared("turret_hostile"), Gun.Properties(3, 250f).fireInterval(0.8f)) {
	init {
		setTargetFetcher { if(world is SinglePlayerWorld) world.player else world.entities.getDistanceSorted(this).firstOrNull { it is Player } as? Player };
		setFollowRange(408f);
		team = "enemies";
	}

	// 공유 자원이라 정리 안 함
	override fun dispose() {}
}
