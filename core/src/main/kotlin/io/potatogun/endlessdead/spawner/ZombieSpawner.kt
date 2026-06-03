package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.position.distanceTo;
import io.potatogun.gdxhelper.world.World;

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
    private var spawnIncreaseTimer = 0f;
    private val spawnIncreaseInterval = 30f

	/**
	 * 매 프레임 실행해서 소환할 시간이 되면 좀비를 스폰한다
	 */
    override fun update(delta: Float) {
        spawnTimer += delta
        if(spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval
            for(i in 1..zombiesPerSpawn)
                spawnRandomZombie()
        }

        /**
        좀비의 스폰 수 증가
        spawnIncreaseInterval = 30f
        찾아보니, delta는 1초 맞아 떨어지는게 아니라 30초에 맞아 떨어지지 않는다여 하여
        spawnIncreaseTimer가 Internal보다 커지면 약 30초가 되는 걸 이용
        타이머가 인터버에 도달하면 -로 다시 처음부터 세며 간격
         */
        spawnIncreaseTimer += delta
        if(spawnIncreaseTimer >= spawnIncreaseInterval) {
            spawnIncreaseTimer -= spawnIncreaseInterval
            if(zombiesPerSpawn < maxZombiesPerSpawn) {
                zombiesPerSpawn++;
                world.viewer?.drawSubtitles("More zombies coming...");
            }
        }
    }

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가하고 반환한다
	 */
    private inline fun spawnRandomZombie() {
		val attackTarget: Player? = world.getRandom<Player>();
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
            rand < 6	-> Zombie.Weak(world, Position(randomX, randomY))		// 60% 확률
            rand < 9	-> Zombie.Normal(world, Position(randomX, randomY))	// 30% 확률
            else		-> Zombie.Strong(world, Position(randomX, randomY))	// 10% 확률
        }.apply {
			target = attackTarget;
		};

        world.addEntity(newZombie);
    }
}
