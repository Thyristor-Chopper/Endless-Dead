package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.gdxhelper.util.EntityListPool;
import io.potatogun.gdxhelper.util.getDistanceSorted;
import io.potatogun.gdxhelper.world.World;

/**
 * 같은 팀이 아닌 개체를 공격해주는 포탑
 *
 * @param world       속한 세계
 * @param name        개체 표시 이름
 * @param x           X 좌표
 * @param y           Y 좌표
 * @param team        포탑의 팀 (null: 중립)
 * @param gun         포탑의 총
 * @param health      포탑의 체력
 * @param isPermanent 포탑이 영구적인지의 여부(죽지 못하는지)
 * @param texture     개체 텍스처
 */
abstract class TeamTurret(world: World, name: String, x: Float, y: Float, team: String?, gun: Item?, health: Int, isPermanent: Boolean = false, texture: Texture) : Turret(world, name, x, y, gun, health, isPermanent, texture) {
	private val entityListPool = EntityListPool(autoClear = false);

	init {
		setTargetFetcher {
			val distanceSorted = entityListPool.obtain();
			world.entities.getDistanceSorted(this, distanceSorted);
			val ret = distanceSorted.firstOrNull { it is LivingEntity && !it.isSameTeamWith(this) && it !is Bullet } as? LivingEntity;
			entityListPool.free(distanceSorted);

			/* return */ ret
		};
		this.team = team;
	}
}
