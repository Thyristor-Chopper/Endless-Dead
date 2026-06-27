package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.gdxhelper.util.getDistanceSorted;
import io.potatogun.gdxhelper.world.World;

/**
 * 좀비를 공격해주는 포탑
 *
 * @param world 속한 세계
 * @param x     X 좌표
 * @param y     Y 좌표
 */
class FriendlyTurret(world: World, x: Float, y: Float) : Turret(world, x, y, Textures.getShared("turret_friendly"), FriendlyTurretGun()) {
	init {
		setTargetFetcher { world.entities.getDistanceSorted(this).firstOrNull { it is Zombie } as? Zombie };
		setFollowRange(456f);
		team = "friends";
	}

	// 공유 자원이라 정리 안 함
	override fun dispose() {}

	/**
	 * 이 터렛의 발사기
	 */
	private class FriendlyTurretGun : Gun("friendly_turret_shooter", "Friendly Turret Shooter", Gun.Properties(2, 1000f).fireInterval(0.4f).rarity(Rarity.RARE) as Gun.Properties);
}
