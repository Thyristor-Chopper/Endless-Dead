package io.potatogun.endlessdead.item

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.world.Freezable;

/**
 * 시간 정지기
 */
class TimeStopper : Item("time_stopper", "Time Stopper", Item.Properties().rarity(Rarity.RARE)), Usable {
	override val isContinuousUseAllowed = false;

	// 타이머를 사용해서 시간을 3초 멈춘다.
	override fun use(user: InventoryHolder): Boolean {
		if(!user.inventory.hasItem(this)) return false;
		if(user !is Player) return false;
		val world = user.world;
		val viewer = world.viewer as? SubtitlesDrawable;
		if(world !is Freezable) {
			viewer?.drawSubtitles("Can't use this item here");
			return false;
		}
		world.freeze(3f) { viewer?.drawSubtitles("Time moves again") };
		viewer?.drawSubtitles("Time stop!");
		destroy();
		return true;
	}
}
