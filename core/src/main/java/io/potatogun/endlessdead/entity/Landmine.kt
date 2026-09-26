package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.Textures;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.rotateToRandom;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.world.World;

/**
 * 지뢰
 *
 * @param    world        지뢰가 있는 세계
 * @param    x            지뢰의 X 위치
 * @param    y            지뢰의 Y 위치
 * @property installer    설치한 개체
 * @property damage       지뢰가 주는 피해량
 * @param    health       지뢰 체력 (초 단위 지속 시간)
 * @param    size         지뢰 지름
 * @param    texture      지뢰 텍스처
 */
class Landmine @JvmOverloads constructor(world: World, x: Float, y: Float, val installer: Entity, val damage: Int, health: Int, size: Float = 32f, texture: Texture = Textures.getShared("landmine")) : LivingEntity(world, "Landmine", x, y, size, size, health, texture), BodyDamagable {
	override val damageInvincibilityDuration = 0.1f;
	override val bodyDamage = damage;
	val timers = TimerManager();

	init {
		rotateToRandom();

		timers.register(RepeatingTimer(1f) {
			takeDamage(1, damageIndicator = LivingEntity.DamageIndicatorOption.HIDE);
		});

		if(installer is TeamMember)
			team = installer.team;
	}

	override fun update(delta: Float) {
		super.update(delta);

		timers.update(delta);
	}
}
