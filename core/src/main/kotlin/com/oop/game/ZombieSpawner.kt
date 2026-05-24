package com.oop.game

import com.oop.game.entity.NormalZombie;
import com.oop.game.entity.Player;
import com.oop.game.entity.StrongZombie;
import com.oop.game.entity.WeakZombie;
import com.oop.game.entity.Zombie;
import com.oop.game.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @param world			소속 세계
 * @param spawnInterval	소환 간격
 */
class ZombieSpawner(val world: World, val spawnInterval: Float = 3f) {
    private var timer = 0f

    fun tick(delta: Float): Zombie? {
        timer += delta
        if (timer >= spawnInterval) {
            timer -= spawnInterval
            return spawnRandomZombie()
        }
        return null
    }

    private fun spawnRandomZombie(): Zombie {
        val randomX = Random.nextFloat() * (world.width - 70f);
        val randomY = Random.nextFloat() * (world.height - 70f);

        // 주사위를 굴려서 확률로 좀비 종류 뽑기
        val rand = Random.nextInt(10);
        val newZombie = when {
            rand < 6 	-> WeakZombie(world, randomX, randomY, world.player, angle = 10f)		// 60% 확률
            rand < 9	-> NormalZombie(world, randomX, randomY, world.player, angle = 10f)	// 30% 확률
            else		-> StrongZombie(world, randomX, randomY, world.player, angle = 10f)	// 10% 확률
        }

        world.add(newZombie)
        return newZombie
    }
}
