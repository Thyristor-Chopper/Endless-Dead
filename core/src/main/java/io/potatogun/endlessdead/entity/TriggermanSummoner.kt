package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.component.ItemDropComponent;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.inventory.LinearInventory;
import io.potatogun.endlessdead.item.TurboStreamliner;
import io.potatogun.endlessdead.item.TurretInstaller;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
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
class TriggermanSummoner(world: World, x: Float, y: Float) : Summoner(world, "Triggerman Summoner", x, y, 26f, 32f, 8000, Textures.getShared("triggerman_summoner")), InventoryHolder, DamageListener {
	override val damageInvincibilityDuration = 0.15f;
	override val isActive: Boolean
		get() = (findPlayer() != null);
	override val inventory = LinearInventory();
	private val dropComponent = ItemDropComponent(this);
	override val summonInterval = 5f;

	init {
		setOriginOffsetY(-3f);

		// 30초마다 300 피 회복
		timers.register(RepeatingTimer(30f) {
			heal(300);
		});

		// 처치 시 보상
		if(Random.nextInt(4) == 0)  // 25% 확률로 터보 스트림라이너
			inventory.addItem(TurboStreamliner());
		for(i in 1..(Random.nextInt(21) + 10))  // 10~30개의 포탑설치기
			inventory.addItem(TurretInstaller());
	}

	override fun summon() {
		if(Random.nextInt(2) != 0) return;  // 50% 확률로 소환
		val rad = toRadians(getRotationAngle().toDouble() + 90.0 + Random.nextInt(30).toDouble() - 15.0).toFloat();
		val distance = 32f;
		val triggerman = Triggerman(world, x + distance * cos(rad), y + distance * sin(rad));  // 소환기가 있는 위치에 생성. 소환기 텍스처에 구멍이 있고 거기서 총잡이가 월드로 나온다는 컨셉이다.
		world.entities.add(triggerman);
	}

	// 죽으면 보상 떨구기
	override fun onDeath(killer: Entity?) {
		dropComponent.dropAll();
	}
}
