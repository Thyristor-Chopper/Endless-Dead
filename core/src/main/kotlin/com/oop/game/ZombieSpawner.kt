package com.oop.game

import com.oop.game.entity.Player;
import com.oop.game.entity.Zombie;
import com.oop.game.world.World;

class ZombieSpawner(private val world: World, private val player: Player, private val spawnInterval: Float = 3f) {
    private var timer = 0f

    fun update(delta: Float): Zombie? {
        timer += delta
        if (timer >= spawnInterval) {
            timer -= spawnInterval
            return spawnRandomZombie()
        }
        return null
    }

    private fun spawnRandomZombie(): Zombie {
        val randomX = kotlin.random.Random.nextFloat() * (world.width - 70f)
        val randomY = kotlin.random.Random.nextFloat() * (world.height - 70f)

        // 주사위를 굴려서 확률로 좀비 종류 뽑기
        val rand = kotlin.random.Random.nextInt(100)
        val newZombie = when {
            rand < 60 	-> Zombie.WeakZombie(world, randomX, randomY, player,angle=10f)	// 60% 확률
            rand < 90	-> Zombie.NormalZombie(world, randomX, randomY, player,angle=10f)	// 30% 확률
            else		-> Zombie.StrongZombie(world, randomX, randomY, player,angle=10f)	// 10% 확률
        }

        world.add(newZombie)
        return newZombie
    }
}
