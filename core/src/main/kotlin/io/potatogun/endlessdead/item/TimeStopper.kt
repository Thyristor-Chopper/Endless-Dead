package io.potatogun.endlessdead.item

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

/**
 * 시간 정지기
 *
 * @param world	아이템이 있는 세계
 */
class TimeStopper(world: World) : Item(world, "time_stopper", "Time Stopper"), Usable {
    override val isContinuousUseAllowed = false;

    /**
     * 타이머를 사용해서 시간을 3초 멈춘다
     */
    override fun use(): Boolean {
		val holder: Entity? = this.holder;
		if(holder is Player) {
			if(world !is Freezable) {
				world.viewer?.drawSubtitles("Can't use this item here");
				return false;
			}
			world.freeze(3f);
			world.viewer?.drawSubtitles("Time stop!");
			destroy();
			return true;
		}
		return false;
    }
}
