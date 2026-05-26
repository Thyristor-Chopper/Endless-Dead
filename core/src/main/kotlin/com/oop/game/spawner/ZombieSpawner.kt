package com.oop.game.spawner;

import com.oop.game.Position;
import com.oop.game.entity.Player;
import com.oop.game.entity.Zombie;
import com.oop.game.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @param world			소속 세계
 * @param spawnInterval	소환 간격
 */
class ZombieSpawner(override val world: World, val spawnInterval: Float = 3f) : Spawner {
    private var timer = 0f;

	/**
	 * 매 프레임 실행해서 소환할 시간이 되면 좀비를 스폰한다
	 */
    override fun tick(delta: Float) {
        timer += delta;
        if(timer >= spawnInterval) {
            timer -= spawnInterval;
            spawnRandomZombie();
        }
    }

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가하고 반환한다
	 */
    private inline fun spawnRandomZombie(): Zombie {
        var randomX: Float;
        var randomY: Float;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
		} while(Position(randomX, randomY).distanceTo(world.player.position) < 64.0f);

        // 주사위를 굴려서 확률로 좀비 종류 뽑기
        val rand = Random.nextInt(10);
        val newZombie = when {
            rand < 6	-> Zombie.weak(world, randomX, randomY, world.player, angle = 10f)		// 60% 확률
            rand < 9	-> Zombie.normal(world, randomX, randomY, world.player, angle = 10f)	// 30% 확률
            else		-> Zombie.strong(world, randomX, randomY, world.player, angle = 10f)	// 10% 확률
        };

        world.add(newZombie);
        return newZombie;
    }
}
