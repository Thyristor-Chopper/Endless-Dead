package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.zombie.GiantZombie;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.position.distanceTo;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.Math;
import io.potatogun.gdxhelper.util.nextFloat;
import io.potatogun.gdxhelper.util.nextSign;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.cos;
import kotlin.math.PI;
import kotlin.math.sin;
import kotlin.random.Random;

/**
 * 왕좀비 소환기
 *
 * @property world 소속 월드
 */
class GiantZombieSpawner(world: World) : Spawner(world) {
	private val spawnInterval = 300f;
	private val timers = TimerManager();

	init {
		timers.register(RepeatingTimer(spawnInterval, this::spawn));
	}

	override fun update(delta: Float) {
		timers.tick(delta);
	}

	private fun spawn() {
		val newZombie = GiantZombie(world, 0f, 0f);
		val target = newZombie.target;
		if(target != null) {
			val rad = Random.nextFloat(0f, 2 * PI.toFloat());
			val maxLength = Math.max2(target.width, target.height);
			val distance = Random.nextFloat(maxLength + 96f, maxLength + 224f) * Random.nextSign();
			newZombie.position.set(target.x + distance * cos(rad), target.y + distance * sin(rad));
		}
		world.entities.add(newZombie);
	}
}
