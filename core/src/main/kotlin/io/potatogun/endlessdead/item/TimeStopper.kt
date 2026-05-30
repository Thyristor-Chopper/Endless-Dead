package com.oop.game.item

import com.oop.game.world.Freezable;
import com.oop.game.world.World;

/**
 * 시계 - 시간정지 아이템
 *
 * @param world	아이템이 있는 세계
 */
class TimeStopper(world: World) : Item(world, "time_stopper", "Time Stopper"), Usable {
    override val allowContinuousUse = false;

    /**
     * 타이머를 사용해서 시간을 3초 멈춘다
     */
    override fun use(): Boolean {
		if(!(world is Freezable)) {
			world.drawSubtitles("Can't use this item here");
			return false;
		}
		
        world.freeze(3f);
        world.drawSubtitles("Time stop!");
        destroy();
        return true;
    }
}
