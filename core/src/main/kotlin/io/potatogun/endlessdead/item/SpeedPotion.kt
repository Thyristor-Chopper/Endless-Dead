package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.world.World;

/**
 * 속도 포션 - 속도 증가 아이템
 *
 * @param world	아이템이 있는 세계
 */
class SpeedPotion(world: World) : Item(world, "speed_potion", "Speed Potion"), Usable {
	override val allowContinuousUse = false;
	
	/**
	 * 포션을 사용하여 속도를 1만큼 올린다
	 */
	override fun use(): Boolean {
		world.player.speedUp(20f, 30f);
		world.drawSubtitles("SPEED UP");
		destroy();
		return true;
	}
}
