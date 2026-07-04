package io.potatogun.endlessdead.item

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.world.Freezable;

/**
 * 시간 정지기 아이템
 */
class TimeStopper : Item("time_stopper", "Time Stopper", Item.Properties().rarity(Rarity.UNCOMMON)), Usable {
	override val isContinuousUseAllowed = false;

	// 타이머를 사용해서 시간을 3초 멈춘다.
	override fun use(user: InventoryHolder): Boolean {
		if(!user.inventory.hasItem(this)) return false;
		if(user !is Player) return false;
		val world = user.position.world;
		if(world !is Freezable) {
			world.projector?.drawSubtitles("Can't use this item here");
			return false;
		}
		world.projector?.drawSubtitles("Time stop!");
		world.freeze(3f);
		destroy();
		return true;
	}
}
