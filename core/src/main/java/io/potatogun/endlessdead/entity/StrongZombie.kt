package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.entity.ai.DashToTarget;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.world.World;

/**
 * 강한 좀비
 *
 * @param world 개체가 속한 세계
 * @param x     개체의 처음 X 위치
 * @param y     개체의 처음 Y 위치
 */
class StrongZombie(world: World, x: Float, y: Float) : Zombie(world, "Rabid Zombie", x, y, 49f, 70f, Zombie.Properties(15, 5, 50f)) {
	private val dasher = DashToTarget(this, 20, 800f, 250f);

	init {
		// 강한 좀비는 살짝 붉게
		overlayColor = Utils.rgb(255, 204, 204);
	}

	override fun updateAI(delta: Float) {
		dasher.update(delta);
		if(dasher.state == DashToTarget.State.STANDBY || dasher.state == DashToTarget.State.COOLDOWN)
			super.updateAI(delta);
	}
}
