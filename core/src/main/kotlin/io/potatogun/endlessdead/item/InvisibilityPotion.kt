package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.entity.Entity;
import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.world.World;

/**
 * 투명 포션
 *
 * @param world	아이템이 있는 세계
 */
class InvisibilityPotion(world: World) : Item(world, "invisibility_potion", "Invisibility Potion"), Usable {
	override val allowContinuousUse = false;

	override fun use(): Boolean {
		val holder: InventoryEntity? = this.holder;
		if(holder !is Entity) return false;
		holder.isInvisibleToOthers = true;
		Utils.setTimeout(15f) {
			holder.isInvisibleToOthers = false;
		};
		return true;
	}
}
