package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.FriendlyTurret;
import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;

/**
 * 좀비 공격 포탑 설치기
 */
class TurretInstaller : Item("turret_installer", "Turret Installer", Item.Properties().rarity(Rarity.RARE)), Usable {
	override val isContinuousUseAllowed = false;

	override fun use(user: InventoryHolder): Boolean {
		if(!user.inventory.hasItem(this)) return false;
		if(user !is Entity) return false;
		user.world.entities.add(FriendlyTurret(user.world, user.x, user.y));
		val viewer = user.world.viewer;
		if(user is Player && viewer is SubtitlesDrawable)
			viewer.drawSubtitles("Installed the turret");
		destroy();
		return true;
	}
}
