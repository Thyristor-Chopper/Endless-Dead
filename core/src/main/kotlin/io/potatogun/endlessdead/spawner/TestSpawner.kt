package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Position;
import io.potatogun.endlessdead.entity.Test;
import io.potatogun.endlessdead.world.World;

import kotlin.random.Random;

/**
 * 테스트용
 */
class TestSpawner(world: World, private val spawnInterval: Float = 5f) : Spawner(world) {
    private var spawnTimer = 0f;

	/**
	 * 매 프레임 실행해서 소환할 시간이 되면 좀비를 스폰한다
	 */
    override fun update(delta: Float) {
        spawnTimer += delta;
        if(spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            spawn();
        }
    }

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가하고 반환한다
	 */
    private inline fun spawn() {
        var randomX: Float;
        var randomY: Float;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
		} while(Position(randomX, randomY).distanceTo(world.player.position) < 64f);
        world.addEntity(Test(world, randomX, randomY));
    }
}
