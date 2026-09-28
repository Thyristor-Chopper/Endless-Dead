package io.potatogun.endlessdead.item

import com.badlogic.gdx.graphics.Color;

import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.util.TimerAttachable;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

/**
 * 시간 정지기 아이템
 */
class TimeStopper : Item("time_stopper", "Ultra Stopwatch", Item.Properties().rarity(Rarity.UNCOMMON)), Usable {
	override val isContinuousUseAllowed = false;

	// 타이머를 사용해서 시간을 3초 멈춘다.
	override fun use(user: ItemSelectable): Boolean {
		if(user.selectedItem !== this) return false;
		if(user !is Player) return false;
		val world = user.world;
		if(world !is Freezable || world !is TimerAttachable) {
			world.projector?.drawSubtitles("Can't use this item here", Color.SALMON);
			return false;
		}
		if(world.isFrozen) {
			world.projector?.drawSubtitles("Time is already stopped", Color.SALMON);
			return false;
		}
		world.projector?.drawSubtitles("Time stop!");
		world.freeze();
		world.attachTimer(Timer(3f) {
			world.unfreeze();
		});
		destroy();
		return true;
	}
}
