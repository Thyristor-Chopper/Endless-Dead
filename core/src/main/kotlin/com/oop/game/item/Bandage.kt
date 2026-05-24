package com.oop.game.item;

import com.oop.game.world.World;

/**
 * 붕대 - 회복 아이템
 *
 * @param world	아이템이 있는 세계
 */
class Bandage(world: World) : Item(world, "bandage", "Bandage"), Usable {
	override val allowContinuousUse = false;
	
	/**
	 * 붕대를 사용하여 체력을 10만큼 회복한다
	 */
	override fun use(): Boolean {
		world.player.heal(10);
		world.drawSubtitles("Healed 10 HP");
		destroy();
		return true;
	}
}
