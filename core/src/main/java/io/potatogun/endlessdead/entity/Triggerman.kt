package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.ai.RotateToTarget;
import io.potatogun.endlessdead.entity.ai.ShootTarget;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.world.World;

import kotlin.random.Random;

/**
 * 총잡이 - 총을 쏘는 적
 */
class Triggerman private constructor(world: World, x: Float, y: Float, override val inventory: SingleItemInventory) : Mob(world, "Triggerman", x, y, 32f, 34f, 10, Textures.getShared("triggerman")), InventoryHolder, DamageListener, ItemSelectable by InventoryItemSelector(inventory), PenetratorDamagable, ItemDroppable, ItemPickupable {
	private val rotator = RotateToTarget(this);
	private val shooter = ShootTarget(this, 360f);
	override val movementSpeed = 140f;
	override val penetrationDamage = 1;
	override val damageInvincibilityDuration = 0.15f;
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("canDropItems")
	override val canDropItems = (Random.nextInt(1000) + 1 <= 1);  // 0.1% 확률로 총을 떨굴 수 있음
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("canPickupItems")
	override val canPickupItems = (Random.nextInt(10) + 1 <= 3);  // 30% 확률

	/**
	 * 총잡이를 생성한다.
	 *
	 * @param world 개체가 속한 세계
	 * @param x     개체의 처음 X 위치
	 * @param y     개체의 처음 Y 위치
	 */
	constructor(world: World, x: Float, y: Float) : this(world, x, y, SingleItemInventory());

	init {
		val gun = TriggermanGun();
		inventory.addItem(gun);
		selectItem(gun);
	}

	override fun findNewTarget(): LivingEntity? {
		val world = this.world;
		if(world is SinglePlayerWorld)
			return world.player;
		else
			return world.entities.getClosestOf<Player>(this);
	}

	override fun update(delta: Float) {
		super.update(delta);
		pickupNearbyItems();
		rotator.update(delta);
		shooter.update(delta);
	}

	// 총이 떨어져 있을 때 자신의 총보다 좋으면 갈아끼운다.
	override fun pickupNearbyItems(): Boolean {
		if(!canPickupItems) return false;
		var pickedUp = false;
		world.entities.forEachNearby(this) { entity ->
			if(entity !is DroppedItem) return@forEachNearby;
			if(!collidesWith(entity)) return@forEachNearby;
			val item = entity.item;
			val selected = selectedItem;
			if(item is Gun && (selected !is Gun || item.bulletDamage > selected.bulletDamage)) {
				selected?.let {
					if(canDropItems) dropItem(it);
					else it.destroy();
				};
				entity.pickup(this);
				pickedUp = true;
				selectItem(item);
			}
		};
		return pickedUp;
	}

	// 누군가가 자신을 공격하면 처치 대상을 그자로 한다.
	//   자연 생성된 포탑은 공격 불가이기 때문에 그것에게 공격받아도 그걸 타겟하지는 않는다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			target = attacker;
	}

	/**
	 * 이 개체의 총
	 */
	private class TriggermanGun : Gun("triggerman_gun", "Triggerman's Gun", Gun.Properties(3, 400f).fireInterval(0.5f).bulletTexture(Textures.getShared("silver_bullet")).rarity(Rarity.RARE) as Gun.Properties);
}
