package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.turret.FriendlyTurret;
import io.potatogun.endlessdead.entity.turret.HostileTurret;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.drawSubtitles;

/**
 * 좀비 공격 포탑 설치기 아이템
 */
class TurretInstaller : Item("turret_installer", "Turret Installer", Item.Properties().rarity(Rarity.RARE)), Usable {
	override val isContinuousUseAllowed = false;

	override fun use(user: InventoryHolder): Boolean {
		if(!user.inventory.hasItem(this)) return false;
		if(user !is Entity) return false;
		val world = user.position.world;
		world.entities.add(
			if(user is LivingEntity && user.team == "friends") FriendlyTurret(world, user.x, user.y)  // 플레이어도 포함
			else HostileTurret(world, user.x, user.y)
		);
		if(user is Player)
			world.projector?.drawSubtitles("Installed the turret");
		destroy();
		return true;
	}
}
