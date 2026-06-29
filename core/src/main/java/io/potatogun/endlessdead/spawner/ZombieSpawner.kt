package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.util.Position;
import io.potatogun.gdxhelper.util.RepeatingTimer;
import io.potatogun.gdxhelper.util.TimerManager;
import io.potatogun.gdxhelper.util.distanceTo;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @param    world         소속 세계
 * @property spawnInterval 소환 간격
 */
class ZombieSpawner(world: World, private val spawnInterval: Float) : Spawner(world) {
	private var zombiesPerSpawn = 1
		set(value) {
			if(value < 0) field = 0;
			else field = value;
		};
	private val maxZombiesPerSpawn = 5;
	private val spawnIncreaseTimer: RepeatingTimer;
	private val spawnIncreaseInterval = 60f;
	private val timerManager = TimerManager();

	init {
		timerManager.register(RepeatingTimer(spawnInterval) {
			for(i in 1..zombiesPerSpawn)
				spawnRandomZombie();
		});

		spawnIncreaseTimer = RepeatingTimer(spawnIncreaseInterval) { timer ->
			if(zombiesPerSpawn < maxZombiesPerSpawn) {
				zombiesPerSpawn++;
				(world.viewer as? SubtitlesDrawable)?.drawSubtitles("More zombies coming...");
			} else {
				timerManager.unregister(timer);
			}
		}.also { timerManager.register(it) };
	}

	/**
	 * 매 프레임 실행해서 타이머를 갱신하여 소환할 시간이 되면 좀비를 스폰한다
	 */
	override fun update(delta: Float) {
		timerManager.tick(delta);
	}

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가한다.
	 *   스폰 타이머에서만 한 번 쓰이기 떄문에 inline이다.
	 */
	private inline fun spawnRandomZombie() {
		// 주사위를 굴려서 확률로 좀비 종류 뽑기
		val rand = Random.nextInt(10);
		val newZombie = when {
			rand < 6	-> Zombie.Weak(world, 0f, 0f)		// 60% 확률
			rand < 9	-> Zombie.Normal(world, 0f, 0f)	// 30% 확률
			else		-> Zombie.Strong(world, 0f, 0f)	// 10% 확률
		};

		var randomX: Float;
		var randomY: Float;
		var loopCount = 0;
		val target = newZombie.target;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
			loopCount++;
		} while(target != null && Position(randomX, randomY).distanceTo(target) < 408f && loopCount < 30);

		newZombie.x = randomX;
		newZombie.y = randomY;

		world.entities.add(newZombie);
	}
}
