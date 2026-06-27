package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.util.getDistanceSorted;
import io.potatogun.gdxhelper.world.World;

/**
 * 플레이어를 공격하는 포탑
 *
 * @param world 속한 세계
 * @param x     X 좌표
 * @param y     Y 좌표
 */
class HostileTurret(world: World, x: Float, y: Float) : Turret(world, x, y, Textures.getShared("turret_hostile"), HostileTurretGun()) {
	init {
		setTargetFetcher { if(world is SinglePlayerWorld) world.player else world.entities.getDistanceSorted(this).firstOrNull { it is Player } as? Player };
		setFollowRange(384f);
		// 중립으로...
		// team = "enemies";
	}

	// 공유 자원이라 정리 안 함
	override fun dispose() {}

	/**
	 * 이 터렛의 발사기
	 */
	private class HostileTurretGun : Gun("hostile_turret_shooter", "Hostile Turret Shooter", Gun.Properties(4, 190f).fireInterval(1.0f).bulletTexture(Textures.getShared("gray_bullet"), false).bulletSize(18f).rarity(Rarity.RARE) as Gun.Properties);
}
