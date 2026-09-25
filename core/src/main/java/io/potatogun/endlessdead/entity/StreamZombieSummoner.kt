package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.zombie.WeakZombie;
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
 * 물살처럼 빠르게 좀비를 소환하는 기계. ZombieSpawner와는 다르게 월드에서 보이는 개체이다.
 *
 * @param world 개체가 속한 세계
 * @param x	 개체의 X 위치
 * @param y	 개체의 Y 위치
 */
class StreamZombieSummoner(world: World, x: Float, y: Float) : Summoner(world, "Stream Zombie Summoner", x, y, 26f, 32f, 100, Textures.getShared("stream_zombie_summoner")) {
	private val maxDistanceToPlayer = 456f;
	override val damageInvincibilityDuration = 0.15f;
	override val summonInterval = 0.5f;
	override val isActive: Boolean
		get() {
			val player = findPlayer();
			return player != null && distanceTo(player) <= maxDistanceToPlayer;
		};

	init {
		setOriginOffsetY(-3f);
	}

	override fun update(delta: Float) {
		super.update(delta);

		if(!isActive)
			rotateBy(30f * delta);  // 30 == 0.5 / (1 / 60) - 60fps 기준으로 0.5도씩 회전하되 프레임률에 영향을 받지 않게 한다.
	}

	override fun summon() {
		val rad = toRadians(getRotationAngle().toDouble() + 90.0 + Random.nextInt(30).toDouble() - 15.0).toFloat();
		val distance = 24f;
		val zombie = WeakZombie(world, x + distance * cos(rad), y + distance * sin(rad));  // 소환기가 있는 위치에 생성. 소환기 텍스처에 구멍이 있고 거기서 좀비가 월드로 나온다는 컨셉이다.
		world.entities.add(zombie);
	}
}
