package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.zombie.NormalZombie;
import io.potatogun.endlessdead.entity.zombie.StrongZombie;
import io.potatogun.endlessdead.entity.zombie.WeakZombie;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.position.distanceTo;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @property world 소속 월드
 */
class ZombieSpawner(world: World) : Spawner(world) {
	private val spawnInterval = 3f;
	private var zombiesPerSpawn = 1
		set(value) {
			if(value < 0) field = 0;
			else field = value;
		};
	private val maxZombiesPerSpawn = 5;
	private val spawnIncreaseTimer: RepeatingTimer;
	private val spawnIncreaseInterval = 60f;
	private val timers = TimerManager();

	init {
		timers.register(RepeatingTimer(spawnInterval) {
			for(i in 1..zombiesPerSpawn)
				spawnRandomZombie();
		});

		spawnIncreaseTimer = RepeatingTimer(spawnIncreaseInterval) {
			if(zombiesPerSpawn < maxZombiesPerSpawn) {
				zombiesPerSpawn++;
				world.projector?.drawSubtitles("More zombies coming...");
			} else {
				timers.unregister(spawnIncreaseTimer);
			}
		}.also { timers.register(it) };
	}

	// 매 프레임 실행해서 타이머를 갱신하여 소환할 시간이 되면 좀비를 스폰한다
	override fun update(delta: Float) {
		timers.tick(delta);
	}

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가한다.
	 */
	private inline fun spawnRandomZombie() {  // 스폰 타이머에서만 한 번 쓰이기 떄문에 inline이다.
		// 주사위를 굴려서 확률로 좀비 종류 뽑기
		val rand = Random.nextInt(10);
		val newZombie = when {
			rand < 6	-> WeakZombie(world, 0f, 0f)		// 60% 확률
			rand < 9	-> NormalZombie(world, 0f, 0f)		// 30% 확률
			else		-> StrongZombie(world, 0f, 0f)		// 10% 확률
		};
		var loopCount = 0;
		val target = newZombie.target;
		val position = Pools.position.obtain();
		do {
			position.x = Random.nextFloat() * (world.width - 140f) + 70f;
			position.y = Random.nextFloat() * (world.height - 140f) + 70f;
			loopCount++;
		} while(target != null && position.distanceTo(target) < 408f && loopCount < 30);
		newZombie.position.set(position);
		world.entities.add(newZombie);
		Pools.position.free(position);
	}
}
