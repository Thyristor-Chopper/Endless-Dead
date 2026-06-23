package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;

/**
 * 붕대 - 회복 아이템
 */
class Bandage : Item("bandage", "Bandage"), Usable {
	override val isContinuousUseAllowed = false;

	// 붕대를 사용하여 체력을 10만큼 회복한다.
	override fun use(user: InventoryHolder): Boolean {
		if(user !is LivingEntity) return false;
		user.heal(10);
		if(user is Player)
			(user.world.viewer as? SubtitlesDrawable)?.drawSubtitles("Healed 10 HP");
		destroy();
		return true;
	}
}
