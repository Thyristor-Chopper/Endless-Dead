package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;

/**
 * 포탑 (자동 총알 발사 기계)
 *
 * @param world       속한 세계
 * @param x           X 좌표
 * @param y           Y 좌표
 * @param texture     개체 텍스처
 * @param gunSettings 포탑의 총 옵션
 */
abstract class Turret(world: World, x: Float, y: Float, texture: Texture, gunSettings: Gun.Properties) : Entity(world, x, y, 83f, 154f, texture), InventoryHolder, AttackTargetable {
	private val attacker = AutoTargeter(this);  // 클래스 정의 시 위임자에게 this만 넘길 수 있었어도 이딴 수동 위임같은 뻘짓 안 나오지...
	final override val inventory = SingleItemInventory();
	override var target: LivingEntity?
		get() = attacker.target
		set(value) { attacker.target = value };

	init {
		val identity = System.identityHashCode(this).toString();
		inventory.addItem(object : Gun("turret_gun_${identity}", "Turret ${identity}'s Gun", gunSettings.rarity(Rarity.RARE) as Gun.Properties) {});
	}

	protected fun setTargetFetcher(fetcher: () -> LivingEntity?) {
		attacker.setTargetFetcher(fetcher);
	}

	protected fun setFollowRange(range: Float) {
		attacker.setFollowRange(range);
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
