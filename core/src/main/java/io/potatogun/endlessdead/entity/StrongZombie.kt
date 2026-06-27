package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.entity.ai.Behavior.Result;
import io.potatogun.endlessdead.entity.ai.DashToTarget;
import io.potatogun.endlessdead.entity.ai.DashToTarget.DashState;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.world.World;

class StrongZombie(world: World, x: Float, y: Float) : Zombie(world, x, y, 49f, 70f, Zombie.Properties(15, 5, 50f)) {
	private val dasher = DashToTarget(this, 800f, 250f);
	// 강한 좀비는 살짝 붉게
	override val color = Utils.rgb(255, 204, 204);

	override fun updateAI(delta: Float) {
		dasher.update(delta);
		if(dasher.state == DashState.STANDBY || dasher.state == DashState.COOLDOWN)
			super.updateAI(delta);
	}
}
