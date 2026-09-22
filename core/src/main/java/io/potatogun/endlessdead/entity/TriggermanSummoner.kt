package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toRadians;

import kotlin.math.cos;
import kotlin.math.sin;
import kotlin.random.Random;

/**
 * 총잡이를 소환하는 기계. TriggermanSpawner와는 다르게 월드에서 보이는 개체이다.
 *
 * @param world 개체가 속한 세계
 * @param x	 개체의 X 위치
 * @param y	 개체의 Y 위치
 */
class TriggermanSummoner(world: World, x: Float, y: Float) : LivingEntity(world, "Triggerman Summoner", x, y, 26f, 32f, 8000, Textures.getShared("triggerman_summoner")) {
	private val timers = TimerManager();
	override val damageInvincibilityDuration = 0.15f;
	private val isActive: Boolean
		inline get() = (findPlayer() != null);

	init {
		setOriginOffsetY(-3f);
		rotateToRandom();
		timers.register(RepeatingTimer(5f) {
			// 50% 확률로 소환
			if(Random.nextInt(2) == 1)
				spawn();
		});
	}

	override fun update(delta: Float) {
		super.update(delta);

		timers.tick(delta);

		findPlayer()?.let { rotateTo(it) };
	}

	// 타이머에서 한 번만 쓰이므로 인라인
	private inline fun spawn() {
		if(!isActive) return;
		val rad = toRadians(getRotationAngle().toDouble() + 90.0 + Random.nextInt(30).toDouble() - 15.0).toFloat();
		val distance = 32f;
		val triggerman = Triggerman(world, x + distance * cos(rad), y + distance * sin(rad));  // 소환기가 있는 위치에 생성. 소환기 텍스처에 구멍이 있고 거기서 총잡이가 월드로 나온다는 컨셉이다.
		world.entities.add(triggerman);
	}

	// 한 줄 짜리 함수라 인라인
	private inline fun rotateToRandom() {
		rotate(Random.nextInt(360).toFloat());
	}

	private inline fun findPlayer(): Player? {
		val world = this.world;
		return if(world is SinglePlayerWorld) world.player else world.entities.getClosestOf<Player>(this);
	}
}
