package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Color;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.util.beep2;

/**
 * 붕대 - 회복 아이템
 */
class Bandage : Item("bandage", "Bandage"), Usable {
	private val game = EndlessDead.instance;

	// 붕대를 사용하여 체력을 10만큼 회복한다.
	override fun use(user: ItemSelectable): Boolean {
		if(user.selectedItem !== this) return false;
		if(user !is LivingEntity) return false;
		if(user.health >= user.maxHealth) {
			if(user === game.player) {
				user.world?.projector?.drawSubtitles("Can't heal anymore", Color.SALMON);
				beep2();
			}
			return false;
		}
		user.heal(10);
		if(user === game.player)
			user.world?.projector?.drawSubtitles("Healed 10 HP");
		destroy();
		return true;
	}
}
