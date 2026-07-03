package io.potatogun.endlessdead.entity.turret;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.world.World;

/**
 * 플레이어를 공격하는 포탑
 *
 * @param world       속한 세계
 * @param x           X 좌표
 * @param y           Y 좌표
 * @param isPermanent 포탑이 영구적인지의 여부(죽지 못하는지)
 */
class HostileTurret(world: World, x: Float, y: Float, isPermanent: Boolean = false) : Turret(world, "Turret", x, y, HostileTurretGun(), 600, isPermanent, Textures.getShared("turret_hostile")), DamageListener {
	override val followRange = 384f;

	override fun findNewTarget(): LivingEntity? {
		val world = this.world;
		if(world is SinglePlayerWorld)
			return world.player;
		else
			return world.entities.getClosestOf<Player>(this);
	}

	// 누군가가 포탑을 공격하면 처치 대상을 그자로 한다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			target = attacker;
	}

	/**
	 * 이 터렛의 발사기
	 */
	private class HostileTurretGun : Gun("hostile_turret_shooter", "Hostile Turret Shooter", Gun.Properties(4, 190f).fireInterval(1.0f).bulletTexture(Textures.getShared("silver_bullet")).bulletSize(18f).rarity(Rarity.RARE) as Gun.Properties);
}
