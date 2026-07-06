package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.ai.ApproachTarget;
import io.potatogun.endlessdead.entity.ai.RotateToTarget;
import io.potatogun.endlessdead.entity.ai.ShootTarget;
import io.potatogun.endlessdead.entity.component.AutoTargeter;
import io.potatogun.endlessdead.entity.component.ItemDropComponent;
import io.potatogun.endlessdead.entity.component.ItemPickupComponent;
import io.potatogun.endlessdead.entity.component.MoveComponent;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.inventory.LinearInventory;
import io.potatogun.endlessdead.inventory.ObservableInventory;
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
class Triggerman private constructor(world: World, x: Float, y: Float, override val inventory: ObservableInventory) : LivingEntity(world, "Triggerman", x, y, 32f, 34f, 10, Textures.getShared("triggerman")), InventoryHolder, DamageListener, ItemSelectable by InventoryItemSelector(inventory), PenetratorDamagable, Movable, Targetable {
	private val moveComponent = MoveComponent(this, 140f);
	override val speed: Float by moveComponent::speed;
	private val minDistance = 360f;
	private val rotator = RotateToTarget(this);
	private val approacher = ApproachTarget(this, minDistance);
	private val shooter = ShootTarget(this, minDistance);
	override val penetrationDamage = 1;
	override val damageInvincibilityDuration = 0.15f;
	private val dropComponent: ItemDropComponent<Triggerman>? = if(Random.nextInt(1000) + 1 <= 1) ItemDropComponent(this) else null;  // 0.1% 확률로 총을 떨굴 수 있음
	private val pickupComponent: ItemPickupComponent<Triggerman>? = if(Random.nextInt(10) + 1 <= 3) ItemPickupComponent(this) else null;  // 30% 확률
	private val autoTargeter = AutoTargeter(this) {
		if(world is SinglePlayerWorld)
			world.player
		else
			world.entities.getClosestOf<Player>(this)
	};
	override val target: LivingEntity? by autoTargeter::target;
	override val followRange: Float by autoTargeter::followRange;

	/**
	 * 총잡이를 생성한다.
	 *
	 * @param world 개체가 속한 세계
	 * @param x     개체의 처음 X 위치
	 * @param y     개체의 처음 Y 위치
	 */
	constructor(world: World, x: Float, y: Float) : this(world, x, y, LinearInventory(2));

	init {
		val gun = TriggermanGun();
		inventory.addItem(gun);
		selectItem(gun);
	}

	override fun move(delta: Float, directionX: Float, directionY: Float) {
		moveComponent.move(delta, directionX, directionY);
	}

	override fun update(delta: Float) {
		super.update(delta);
		pickupComponent?.let {
			val selected = selectedItem;
			val succeeded = it.pickupNearbyItems { item -> item is Gun && (selected !is Gun || item.bulletDamage > selected.bulletDamage) };
			if(succeeded)
				selected?.let {
					if(dropComponent != null && Random.nextInt(2) == 0)
						dropComponent.dropItem(it);
					else
						it.destroy();
				};
		};
		rotator.update(delta);
		approacher.update(delta);
		if(approacher.state == ApproachTarget.State.APPROACHED)
			shooter.update(delta);
	}

	// 누군가가 자신을 공격하면 처치 대상을 그자로 한다.
	//   자연 생성된 포탑은 공격 불가이기 때문에 그것에게 공격받아도 그걸 타겟하지는 않는다.
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			autoTargeter.target = attacker;
	}

	override fun onDeath(killer: Entity?) {
		dropComponent?.dropAll();
	}

	/**
	 * 이 개체의 총
	 */
	private class TriggermanGun : Gun("triggerman_gun", "Triggerman's Gun", Gun.Properties(3, 400f).fireInterval(0.5f).bulletTexture(Textures.getShared("silver_bullet")).rarity(Rarity.RARE) as Gun.Properties);
}
