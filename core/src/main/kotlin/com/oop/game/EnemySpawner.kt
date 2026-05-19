package com.oop.game

import com.oop.game.example.Enemy
import com.oop.game.objects.Player

class EnemySpawner(
    private val world: GameWorld,
    private val player: Player,
    private val spawnInterval: Float=3f ) {
    private var timer=0f

    fun update(delta: Float):Enemy? {
        timer += delta
        if (timer >= spawnInterval) {
            timer -= spawnInterval
            return spawnRandomEnemy()
        }
        return null
    }

    private fun spawnRandomEnemy():Enemy {
        val randomX = kotlin.random.Random.nextFloat() * (world.worldWidth - 70f)
        val randomY = kotlin.random.Random.nextFloat() * (world.worldHeight - 70f)

        // 주사위를 굴려서 확률로 좀비 종류 뽑기
        val rand = kotlin.random.Random.nextInt(100)
        val newEnemy = when {
            rand < 60 -> Enemy.WeakZombie(world, randomX, randomY, player,angle=10f)     // 60% 확률
            rand < 90 -> Enemy.NormalZombie(world, randomX, randomY, player,angle=10f)   // 30% 확률
            else -> Enemy.StrongZombie(world, randomX, randomY, player,angle=10f)        // 10% 확률
        }

        world.add(newEnemy)
        return newEnemy
    }
}