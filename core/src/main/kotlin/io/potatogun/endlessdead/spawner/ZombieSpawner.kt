package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.util.Position;
import io.potatogun.gdxhelper.util.RepeatingTimer;
import io.potatogun.gdxhelper.util.TimerManager;
import io.potatogun.gdxhelper.util.distanceTo;
import io.potatogun.gdxhelper.util.getRandom;
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
			else if(value > maxZombiesPerSpawn) field = maxZombiesPerSpawn;
			else field = value;
		};
	private val maxZombiesPerSpawn = 8;
	private val spawnIncreaseTimer: RepeatingTimer;
	private val spawnIncreaseInterval = 30f;
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
	 */
	private inline fun spawnRandomZombie() {
		val attackTarget: Player? = if(world is SinglePlayerWorld) world.player else world.entities.getRandom<Player>();
		if(attackTarget == null) return;
		var randomX: Float;
		var randomY: Float;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
		} while(Position(randomX, randomY).distanceTo(attackTarget) < 64f);

		// 주사위를 굴려서 확률로 좀비 종류 뽑기
		val rand = Random.nextInt(10);
		val newZombie = when {
			rand < 6	-> Zombie.Weak(world, randomX, randomY, initialTarget = attackTarget)		// 60% 확률
			rand < 9	-> Zombie.Normal(world, randomX, randomY, initialTarget = attackTarget)	// 30% 확률
			else		-> Zombie.Strong(world, randomX, randomY, initialTarget = attackTarget)	// 10% 확률
		};

		world.entities.add(newZombie);
	}
}
