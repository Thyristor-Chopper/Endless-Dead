package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 총잡이를 소환하는 기계. TriggermanSpawner와는 다르게 월드에서 보이는 개체이다.
 *
 * @param world 개체가 속한 세계
 * @param x	 개체의 X 위치
 * @param y	 개체의 Y 위치
 */
class TriggermanSummoner(world: World, x: Float, y: Float) : LivingEntity(world, "Triggerman Summoner", x, y, 32f, 32f, 250, Textures.getShared("triggerman_summoner")) {
	private val timerManager = TimerManager();
	override val damageInvincibilityDuration = 0.15f;

	init {
		rotateToRandom();
		timerManager.register(RepeatingTimer(10f, this::spawn));
	}

	override fun update(delta: Float) {
		timerManager.tick(delta);
	}

	private fun spawn() {
		rotateToRandom();
		val triggerman = Triggerman(world, x, y);  // 소환기가 있는 위치에 생성. 소환기 텍스처에 구멍이 있고 거기서 총잡이가 월드로 나온다는 컨셉이다.
		world.entities.add(triggerman);
	}

	// 한 줄 짜리 함수라 인라인
	private inline fun rotateToRandom() {
		rotate(Random.nextInt(360).toFloat());
	}
}
