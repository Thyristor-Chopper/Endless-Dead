package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.forEachNearby;
import io.potatogun.endlessdead.entity.teleportToCenter;
import io.potatogun.endlessdead.entity.summoner.Summoner;
import io.potatogun.endlessdead.entity.turret.Turret;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.rotateToRandom;
import io.potatogun.gdxhelper.world.World;

/**
 * 다른 월드로 워프하는 포탈
 *
 * @param world       개체가 속한 세계
 * @param x           개체의 X 위치
 * @param y           개체의 Y 위치
 * @param destination 이동 대상 월드
 */
class Portal(world: World, x: Float = 0f, y: Float = 0f, private val destination: World) : Entity(world, "Warp Portal", x, y, 80f, 80f, Textures.getShared("portal")) {
	init {
		rotateToRandom();
	}

	override fun update(delta: Float) {
		super.update(delta);

		rotateBy(90f * delta);

		val player = GameManager.player;
		forEachNearby { entity ->
			if(entity is LivingEntity && entity !is Bullet && entity !is Landmine && entity !is Turret && entity !is Summoner && collidesWith(entity)) {
				if(entity === player)
					entity.world?.projector?.loadWorld(destination);
				entity.world = destination;
				entity.teleportToCenter();
			}
		};
	}
}
