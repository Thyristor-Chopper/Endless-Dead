package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Triggerman;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.util.Position;
import io.potatogun.gdxhelper.util.RepeatingTimer;
import io.potatogun.gdxhelper.util.TimerManager;
import io.potatogun.gdxhelper.util.distanceTo;
import io.potatogun.gdxhelper.util.getRandom;
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

    private inline fun spawn() {
		val attackTarget: Player? = if(world is SinglePlayerWorld) world.player else world.entities.getRandom<Player>();
		if(attackTarget == null) return;
        var randomX: Float;
        var randomY: Float;
		var loopCount = 0;
		do {
			randomX = Random.nextFloat() * (world.width - 70f);
			randomY = Random.nextFloat() * (world.height - 70f);
			loopCount++;
		} while(Position(randomX, randomY).distanceTo(attackTarget) < 408f && loopCount < 30);
        world.entities.add(Triggerman(world, randomX, randomY).apply { target = attackTarget });
    }
}
