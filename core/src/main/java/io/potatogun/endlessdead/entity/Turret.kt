package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;

abstract class Turret(world: World, x: Float, y: Float, texture: Texture, gunSettings: Gun.Properties) : Entity(world, x, y, 83f, 154f, texture), InventoryHolder, Attackable {
	/**
	 * 개체 추적 범위 (0: 제한 없음)
	 */
	protected open val followRange = 408f;
	private val attacker = SimpleAttacker(this, followRange);  // 클래스 정의 시 위임자에게 this만 넘길 수 있었어도 이딴 수동 위임같은 뻘짓 안 나오지...
	final override val inventory = SingleItemInventory();
	override var target: LivingEntity?
		get() = attacker.target
		set(value) { attacker.target = value };

	init {
		inventory.addItem(Gun("turret_gun", "Turret's Gun", gunSettings));
	}

	protected fun setTargetFetcher(fetcher: () -> LivingEntity?) {
		attacker.setTargetFetcher(fetcher);
	}

	override fun update(delta: Float) {
		super.update(delta);

		val target: LivingEntity? = this.target;
		if(target == null) return;
		rotateTo(target.position);

		inventory.getItem()?.let {
			if(it !is Gun) return;
			it.shoot(target.position, this);
		};
	}
}
