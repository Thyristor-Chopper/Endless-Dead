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
 * 좀비를 소환하는 기계. ZombieSpawner와는 다르게 월드에서 보이는 개체이다.
 *
 * @param world 개체가 속한 세계
 * @param x	 개체의 X 위치
 * @param y	 개체의 Y 위치
 */
class ZombieSummoner(world: World, x: Float, y: Float) : LivingEntity(world, "Zombie Summoner", x, y, 26f, 38f, 100, Textures.getShared("zombie_summoner")) {
	private val timers = TimerManager();
	private val maxDistanceToPlayer = 456f;
	override val damageInvincibilityDuration = 0.15f;

	init {
		rotate(Random.nextInt(360).toFloat());
		timers.register(RepeatingTimer(0.5f, this::spawn));
	}

	override fun update(delta: Float) {
		super.update(delta);

		timers.tick(delta);

		val player = findPlayer();
		if(player != null && distanceTo(player) <= maxDistanceToPlayer)
			rotateTo(player);
	}

	private fun spawn() {
		val player = findPlayer();
		if(player == null) return;
		if(distanceTo(player) > maxDistanceToPlayer) return;
		val rad = toRadians(getRotationAngle().toDouble() + 90.0 + Random.nextInt(30).toDouble() - 15.0).toFloat();
		val distance = 24f;
		val zombie = WeakZombie(world, x + distance * cos(rad), y + distance * sin(rad));  // 소환기가 있는 위치에 생성. 소환기 텍스처에 구멍이 있고 거기서 좀비가 월드로 나온다는 컨셉이다.
		world.entities.add(zombie);
	}

	private inline fun findPlayer(): Player? {
		val world = this.world;
		return if(world is SinglePlayerWorld) world.player else world.entities.getClosestOf<Player>(this);
	}
}
