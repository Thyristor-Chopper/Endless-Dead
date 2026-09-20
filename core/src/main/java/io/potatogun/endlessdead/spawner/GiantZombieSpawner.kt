package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.zombie.GiantZombie;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.position.distanceTo;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 왕좀비 소환기
 *
 * @property world 소속 월드
 */
class GiantZombieSpawner(private val world: World) : Spawner {
	private val spawnInterval = 300f;
	private val timers = TimerManager();

	init {
		timers.register(RepeatingTimer(spawnInterval, this::spawn));
	}

	override fun update(delta: Float) {
		timers.tick(delta);
	}

	private fun spawn() {
		// 주사위를 굴려서 확률로 좀비 종류 뽑기
		val rand = Random.nextInt(10);
		val newZombie = GiantZombie(world, 0f, 0f);
		var loopCount = 0;
		val target = newZombie.target;
		val position = Pools.position.obtain();
		do {
			position.x = Random.nextFloat() * (world.width - 140f) + 70f;
			position.y = Random.nextFloat() * (world.height - 140f) + 70f;
			loopCount++;
		} while(target != null && position.distanceTo(target) < 408f && loopCount < 30);
		newZombie.x = position.x;
		newZombie.y = position.y;
		world.entities.add(newZombie);
		Pools.position.free(position);
	}
}
