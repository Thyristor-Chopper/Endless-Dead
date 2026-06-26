package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.util.getDistanceSorted;
import io.potatogun.gdxhelper.world.World;

class HostileTurret(world: World, x: Float, y: Float) : Turret(world, x, y, Textures.getShared("turret_hostile"), Gun.Properties(2, 200f).fireInterval(0.8f)) {
	override fun getTargetOrReset(): LivingEntity? {
		val target: LivingEntity? = this.target;
		if(target == null || !target.isAlive) {
			val world = this.world;
			this.target = if(world is SinglePlayerWorld) world.player else world.entities.getDistanceSorted(this).firstOrNull { it is Player } as? Player;
			return null;  // 다음 프레임에...
		}
		return target;
	}

	// 공유 자원이라 정리 안 함
	override fun dispose() {}
}
