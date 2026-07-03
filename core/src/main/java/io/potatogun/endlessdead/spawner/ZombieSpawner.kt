package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.entity.Zombie;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.position.distanceTo;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 좀비 소환기
 *
 * @property world         소속 세계
 * @property spawnInterval 소환 간격
 */
class ZombieSpawner(private val world: World) : Spawner {
	private val spawnInterval = 3f;
	private var zombiesPerSpawn = 1
		set(value) {
			if(value < 0) field = 0;
			else field = value;
		};
	private val maxZombiesPerSpawn = 5;
	private val spawnIncreaseTimer: RepeatingTimer;
	private val spawnIncreaseInterval = 60f;
	private val timerManager = TimerManager();

	init {
		timerManager.register(RepeatingTimer(spawnInterval) {
			for(i in 1..zombiesPerSpawn)
				spawnRandomZombie();
		});

		spawnIncreaseTimer = RepeatingTimer(spawnIncreaseInterval) {
			if(zombiesPerSpawn < maxZombiesPerSpawn) {
				zombiesPerSpawn++;
				world.projector?.drawSubtitles("More zombies coming...");
			} else {
				stopSpawnIncreaser();
			}
		}.also { timerManager.register(it) };
	}

	// RepeatingTimer 실행기 내부에서 직접 호출하면 변수 초기화 안 됐다고 컴파일러가 귀찮게 함 자바는 내가 알아서 관리하게 냅두는데 코틀린은 '안전'같은 명목 내세우면서 다 막고 진짜 ㅡㅡ;;
	private inline fun stopSpawnIncreaser() {
		timerManager.unregister(spawnIncreaseTimer);
	}

	// 매 프레임 실행해서 타이머를 갱신하여 소환할 시간이 되면 좀비를 스폰한다
	override fun update(delta: Float) {
		timerManager.tick(delta);
	}

	/**
	 * 무작위로 좀비 종류를 골라서 월드에 추가한다.
	 *   스폰 타이머에서만 한 번 쓰이기 떄문에 inline이다.
	 */
	private inline fun spawnRandomZombie() {
		// 주사위를 굴려서 확률로 좀비 종류 뽑기
		val rand = Random.nextInt(10);
		val newZombie = when {
			rand < 6	-> Zombie.Weak(world, 0f, 0f)		// 60% 확률
			rand < 9	-> Zombie.Normal(world, 0f, 0f)	// 30% 확률
			else		-> Zombie.Strong(world, 0f, 0f)	// 10% 확률
		};
		var loopCount = 0;
		val target = newZombie.target;
		val position = Pools.position.obtain();
		do {
			position.x = Random.nextFloat() * (world.width - 70f);
			position.y = Random.nextFloat() * (world.height - 70f);
			loopCount++;
		} while(target != null && position.distanceTo(target) < 408f && loopCount < 30);
		newZombie.x = position.x;
		newZombie.y = position.y;
		world.entities.add(newZombie);
		Pools.position.free(position);
	}
}
