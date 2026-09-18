package io.potatogun.endlessdead.item

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;

import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.timer.Timer;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

/**
 * 시간 정지기 아이템
 */
class TimeStopper : Item("time_stopper", "Time Stopper", Item.Properties().rarity(Rarity.UNCOMMON)), Usable {
	override val isContinuousUseAllowed = false;

	// 타이머를 사용해서 시간을 3초 멈춘다.
	override fun use(user: ItemSelectable): Boolean {
		if(user.selectedItem !== this) return false;
		if(user !is Player) return false;
		val world = user.getWorld();
		if(world !is Freezable) {
			world.projector?.drawSubtitles("Can't use this item here");
			return false;
		}
		if(world.isFrozen) {
			world.projector?.drawSubtitles("Time is already stopped", Color.SALMON);
			return false;
		}
		world.projector?.drawSubtitles("Time stop!");
		world.freeze();
		GameManager.globalTimers.register(Timer(3f, { GameManager.isPlaying }) {
			world.unfreeze();
			unfreezeTimers.remove(world);
		}.also { unfreezeTimers.put(world, it) });
		destroy();
		return true;
	}

	companion object {
		@JvmSynthetic internal val unfreezeTimers = ObjectMap<World, Timer>(8);
	}
}
