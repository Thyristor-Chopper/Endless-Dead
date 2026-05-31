package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Timer;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.position.distanceTo;
import io.potatogun.endlessdead.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @param world			소속 세계
 * @param spawnInterval	소환 간격
 */
class ZombieSpawner(world: World, private val spawnInterval: Float) : Spawner(world) {
    private var spawnTimer = 0f;
    private var zombiesPerSpawn = 1;
    private val maxZombiesPerSpawn = 8;
    private val spawnIncreaseTimer: Timer;

    init {
        spawnIncreaseTimer = Timer(30f) {
            if(zombiesPerSpawn < maxZombiesPerSpawn) {
                zombiesPerSpawn++;
                world.viewer.drawSubtitles("More zombies coming...");
            }
        }.register();
    }

	/**
	 * 매 프레임 실행해서 소환할 시간이 되면 좀비를 스폰한다
	 */
    override fun update(delta: Float) {
        spawnTimer += delta;
        if(spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            for(i in 1..zombiesPerSpawn)
                spawnRandomZombie();
        }
    }

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가하고 반환한다
	 */
    private inline fun spawnRandomZombie() {
        var randomX: Float;
        var randomY: Float;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
		} while(Position(randomX, randomY).distanceTo(world.player) < 64f);

        // 주사위를 굴려서 확률로 좀비 종류 뽑기
        val rand = Random.nextInt(10);
        val newZombie = when {
            rand < 6	-> Zombie.Weak(world, Position(randomX, randomY))		// 60% 확률
            rand < 9	-> Zombie.Normal(world, Position(randomX, randomY))	// 30% 확률
            else		-> Zombie.Strong(world, Position(randomX, randomY))	// 10% 확률
        };

        world.addEntity(newZombie);
    }

	/**
	 * 타이머 해제
	 */
	override fun cleanUp() {
		spawnIncreaseTimer.unregister();
	}
}
