package com.oop.game.item;

import com.oop.game.world.World;

class Bandage(world: World) : Item(world, "bandage", "Bandage"), Usable {
	override val allowContinuousUse = false;
	
	override fun use(): Boolean {
		world.player.heal(10);
		world.drawSubtitles("Healed 10 HP");
		destroy();
		return true;
	}
}
