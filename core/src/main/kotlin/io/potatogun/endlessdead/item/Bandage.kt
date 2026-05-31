package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.world.World;

/**
 * 붕대 - 회복 아이템
 *
 * @param world	아이템이 있는 세계
 */
class Bandage(world: World) : Item(world, "bandage", "Bandage"), Usable {
	override val allowContinuousUse = false;

	/**
	 * 붕대를 사용하여 체력을 10만큼 회복한다
	 */
	override fun use(): Boolean {
		val holder: InventoryEntity? = this.holder;
		if(holder is LivingEntity) {
			holder.heal(10);
			if(holder is Player)
				world.viewer.drawSubtitles("Healed 10 HP");
			destroy();
			return true;
		}
		return false;
	}
}
