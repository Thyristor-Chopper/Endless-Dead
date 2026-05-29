package com.oop.game.spawner;

import com.oop.game.Position;
import com.oop.game.Timer;
import com.oop.game.entity.Player;
import com.oop.game.entity.Zombie;
import com.oop.game.world.Freezable;
import com.oop.game.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @param world			소속 세계
 * @param spawnInterval	소환 간격
 */
class ZombieSpawner(override val world: World, val spawnInterval: Float = 3f) : Spawner {
    private var timer = 0f
    private var zombiesPerSpawn = 1
    private val maxZombiesPerSpawn = 8
    private val timers = mutableListOf<Timer>()

    init {
        timers.add(Timer(30f) {
            if(zombiesPerSpawn < maxZombiesPerSpawn) {
                zombiesPerSpawn++
                world.drawSubtitles("좀비가 더 많이 몰려옵니다")
            }
        }.register())
    }
	/**
	 * 매 프레임 실행해서 소환할 시간이 되면 좀비를 스폰한다
	 */
    override fun tick(delta: Float) {
        timer += delta
        if(timer >= spawnInterval) {
            timer -= spawnInterval
            spawnRandomZombie()
        }
        var count = 0
        while(count < zombiesPerSpawn) {
            spawnRandomZombie()
            count++
        }
    }

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가하고 반환한다
	 */
    private inline fun spawnRandomZombie() {
		if(world is Freezable && world.isFrozen) return;
	
        var randomX: Float;
        var randomY: Float;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
		} while(Position(randomX, randomY).distanceTo(world.player.position) < 64f);

        // 주사위를 굴려서 확률로 좀비 종류 뽑기
        val rand = Random.nextInt(10);
        val newZombie = when {
            rand < 6	-> Zombie.Weak(world, randomX, randomY, world.player, angle = 10f)		// 60% 확률
            rand < 9	-> Zombie.Normal(world, randomX, randomY, world.player, angle = 10f)	// 30% 확률
            else		-> Zombie.Strong(world, randomX, randomY, world.player, angle = 10f)	// 10% 확률
        }

        world.addEntity(newZombie)
    }
	
	override fun cleanUp() {
		for(timer in timers)
			timer.unregister();
	}
}
