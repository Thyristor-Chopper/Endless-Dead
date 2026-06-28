package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Triggerman;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.util.Position;
import io.potatogun.gdxhelper.util.RepeatingTimer;
import io.potatogun.gdxhelper.util.TimerManager;
import io.potatogun.gdxhelper.util.distanceTo;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

class TriggermanSpawner(world: World, private val spawnInterval: Float = 5f) : Spawner(world) {
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
        var randomX: Float;
        var randomY: Float;
		var loopCount = 0;
		val target = triggerman.target;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
			loopCount++;
		} while(target != null && Position(randomX, randomY).distanceTo(target) < 408f && loopCount < 30);
		triggerman.x = randomX;
		triggerman.y = randomY;
        world.entities.add(triggerman);
    }
}
