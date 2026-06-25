package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;

/**
 * 속도 포션 - 속도 증가 아이템
 */
class SpeedPotion : Item("speed_potion", "Speed Potion", Item.Properties().rarity(Rarity.UNCOMMON)), Usable {
	override val isContinuousUseAllowed = false;

	// 포션을 사용하여 속도를 1만큼 올린다.
	override fun use(user: InventoryHolder): Boolean {
		if(!user.inventory.hasItem(this)) return false;
		if(user !is Player) return false;
		user.speedUp(20f, 30f);
		(user.world.viewer as? SubtitlesDrawable)?.drawSubtitles("SPEED UP");
		destroy();
		return true;
	}
}
