package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.zombie.GiantZombie;
import io.potatogun.gdxhelper.util.max2;
import io.potatogun.gdxhelper.util.nextFloat;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.cos;
import kotlin.math.PI;
import kotlin.math.sin;
import kotlin.random.Random;

/**
 * 왕좀비 소환 시스템
 *
 * @property world 소속 월드
 */
class GiantZombieSpawner(world: World) : Spawner(world) {
	override val spawnInterval = 300f;

	override fun spawn() {
		val newZombie = GiantZombie(world, 0f, 0f);
		val target = newZombie.target;
		if(target != null) {
			val rad = Random.nextFloat(0f, 2f * PI.toFloat());
			val maxLength = (max2(target.width, target.height) + max2(newZombie.width, newZombie.height)) * 0.5f;
			val distance = Random.nextFloat(maxLength + 80f, maxLength + 144f);
			newZombie.teleport(target.x + distance * cos(rad), target.y + distance * sin(rad));
		}
		world.entities.add(newZombie);
	}
}
