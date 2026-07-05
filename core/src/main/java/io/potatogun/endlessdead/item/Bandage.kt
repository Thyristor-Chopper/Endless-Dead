package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.drawSubtitles;

/**
 * 붕대 - 회복 아이템
 */
class Bandage : Item("bandage", "Bandage"), Usable {
	override val isContinuousUseAllowed = false;

	// 붕대를 사용하여 체력을 10만큼 회복한다.
	override fun use(user: ItemSelectable): Boolean {
		if(user.selectedItem !== this) return false;
		if(user !is LivingEntity) return false;
		user.heal(10);
		if(user is Player)
			user.getWorld().projector?.drawSubtitles("Healed 10 HP");
		destroy();
		return true;
	}
}
