package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.Gdx;

import io.potatogun.endlessdead.entity.turret.HostileTurret;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.world.World;

/**
 * 열면 공격포탑을 생성하고 사라지는 함정 상자
 *
 * @param world       개체가 속한 세계
 * @param x           개체의 처음 X 위치
 * @param y           개체의 처음 Y 위치
 * @param initialItem 처음 들어있는 아이템
 */
class TrapChest(world: World, x: Float, y: Float, initialItem: Item? = null): Chest(world, x, y, initialItem) {
	init {
		inventory.addItemRemoveObserver {
			Gdx.app.postRunnable {
				val viewer = world.viewer as? SubtitlesDrawable;
				viewer?.drawSubtitles("Turret trap activated!");
			};
			world.entities.add(HostileTurret(world, x, y));
			remove();
		};
	}
}
