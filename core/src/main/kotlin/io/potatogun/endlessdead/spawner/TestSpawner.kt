package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.Test;
import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.position.distanceTo;
import io.potatogun.endlessdead.world.World;

import kotlin.random.Random;

/**
 * 테스트용
 */
class TestSpawner(world: World, private val spawnInterval: Float = 5f) : Spawner(world) {
    private var spawnTimer = 0f;

    override fun update(delta: Float) {
        spawnTimer += delta;
        if(spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            spawn();
        }
    }

    private inline fun spawn() {
        var randomX: Float;
        var randomY: Float;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
		} while(Position(randomX, randomY).distanceTo(world.player) < 64f);
        world.addEntity(Test(world, Position(randomX, randomY)));
    }
}
