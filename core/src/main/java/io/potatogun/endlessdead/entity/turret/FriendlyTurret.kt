package io.potatogun.endlessdead.entity.turret;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.gdxhelper.world.World;

/**
 * 좀비(정확히는 플레이어 팀이 아닌 개체)를 공격해주는 포탑
 *
 * @param world       속한 세계
 * @param x           X 좌표
 * @param y           Y 좌표
 * @param isPermanent 포탑이 영구적인지의 여부(죽지 못하는지)
 */
class FriendlyTurret(world: World, x: Float, y: Float, isPermanent: Boolean = false) : TeamTurret(world, "Friendly Turret", x, y, "friends", FriendlyTurretGun(), 456f, 2000, isPermanent, Textures.getShared("turret_friendly")) {
	/**
	 * 이 터렛의 발사기
	 */
	private class FriendlyTurretGun : Gun("friendly_turret_shooter", "Friendly Turret Shooter", Gun.Properties(2, 1050f).fireInterval(0.4f).rarity(Rarity.RARE) as Gun.Properties);
}
