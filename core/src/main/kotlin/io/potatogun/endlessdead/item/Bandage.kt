package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.world.World;

/**
 * 붕대 - 회복 아이템
 *
 * @param world	아이템이 있는 세계
 */
class Bandage(world: World) : Item(world, "bandage", "Bandage"), Usable {
	override val isContinuousUseAllowed = false;

	/**
	 * 붕대를 사용하여 체력을 10만큼 회복한다
	 */
	override fun use(): Boolean {
		val holder: Entity? = this.holder;
		if(holder is LivingEntity) {
			holder.heal(10);
			if(holder is Player)
				(world.viewer as? SubtitlesDrawable)?.drawSubtitles("Healed 10 HP");
			destroy();
			return true;
		}
		return false;
	}
}
