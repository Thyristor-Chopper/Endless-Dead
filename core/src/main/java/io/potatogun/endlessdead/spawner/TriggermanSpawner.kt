package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Triggerman;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.position.distanceTo;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 총잡이 소환기
 *
 * @property world 소속 월드
 */
class TriggermanSpawner(private val world: World) : Spawner {
	private val spawnInterval = 5f;
	private val timerManager = TimerManager();

	init {
		timerManager.register(RepeatingTimer(spawnInterval) {
			// 50% 확률로 소환
			if(Random.nextInt(2) == 1)
				spawn();
		});
	}

    override fun update(delta: Float) {
		timerManager.tick(delta);
    }

	// 타이머에서 한 번만 쓰이므로 인라인
    private inline fun spawn() {
		val triggerman = Triggerman(world, 0f, 0f);
		var loopCount = 0;
		val target = triggerman.target;
		val position = world.mutablePositionPool.obtain();
		do {
			position.x = Random.nextFloat() * (world.width - 70f);
			position.y = Random.nextFloat() * (world.height - 70f);
			loopCount++;
		} while(target != null && position.distanceTo(target) < 408f && loopCount < 30);
		triggerman.x = position.x;
		triggerman.y = position.y;
        world.entities.add(triggerman);
		world.mutablePositionPool.free(position);
    }
}
