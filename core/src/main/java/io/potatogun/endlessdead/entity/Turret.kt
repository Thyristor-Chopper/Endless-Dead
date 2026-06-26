package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;

abstract class Turret(world: World, x: Float, y: Float, texture: Texture, gunSettings: Gun.Properties) : Entity(world, x, y, 83f, 154f, texture), InventoryHolder {
	final override val inventory = SingleItemInventory();
	/**
	 * 공격 대상
	 *   자바에서는 getTarget()과 setTarget() 사용
	 */
	var target: LivingEntity? = null;

	init {
		inventory.addItem(Gun("turret_gun", "Turret's Gun", gunSettings));
	}

	/**
	 * 공격 대상을 가져오거나 대상이 사라지면 초기화한다.
	 *   기본 공격 대상 선정 방식을 바꿀 수도 있으므로 open이다.
	 *
	 * @return 공격 대상
	 */
	protected abstract fun getTargetOrReset(): LivingEntity?;

	override fun update(delta: Float) {
		super.update(delta);

		val target: LivingEntity? = getTargetOrReset();
		if(target == null) return;
		rotateTo(target.position);

		val distance = distanceTo(target);
		if(distance > 408f) return;

		inventory.getItem()?.let {
			if(it !is Gun) return;
			it.shoot(target.position, this);
		};
	}
}
