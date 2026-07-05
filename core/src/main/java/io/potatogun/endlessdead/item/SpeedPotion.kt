package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.drawSubtitles;

/**
 * 속도 포션 - 속도 증가 아이템
 */
class SpeedPotion : Item("speed_potion", "Speed Potion", Item.Properties().rarity(Rarity.UNCOMMON)), Usable {
	override val isContinuousUseAllowed = false;

	// 포션을 사용하여 속도를 1만큼 올린다.
	override fun use(user: ItemSelectable): Boolean {
		if(user.selectedItem !== this) return false;
		if(user !is Player) return false;
		user.increaseSpeed(20f, 30f);
		user.getWorld().projector?.drawSubtitles("SPEED UP");
		destroy();
		return true;
	}
}
