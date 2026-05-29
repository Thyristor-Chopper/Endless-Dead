package com.oop.game.item;

import com.oop.game.world.World;

/**
 * 신발 - 속도 아이템
 *
 * @param world	아이템이 있는 세계
 */
class Shoes(world: World) : Item(world, "shoes", "Shoes"), Usable {
	override val allowContinuousUse = false;
	
	/**
	 * 신발을 사용하여 속도를 1만큼 올린다
	 */
	override fun use(): Boolean {
		world.player.speedUp(20f);
		world.drawSubtitles("SPEED UP");
		destroy();
		return true;
	}
}
