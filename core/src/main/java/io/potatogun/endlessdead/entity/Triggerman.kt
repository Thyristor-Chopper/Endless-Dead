package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.ai.ApproachTarget;
import io.potatogun.endlessdead.entity.listener.DamageListener;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Rarity;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.getClosestOf;
import io.potatogun.gdxhelper.world.World;

/**
 * 총을 쏘는 적
 */
class Triggerman private constructor(world: World, x: Float, y: Float, override val inventory: SingleItemInventory) : Mob(world, "Triggerman", x, y, 32f, 34f, 10, Textures.getShared("triggerman")), InventoryHolder, PenetratorDamagable, DamageListener, ItemSelectable by InventoryItemSelector(inventory) {
	private val approacher = ApproachTarget(this, 360f);
	override val movementSpeed = 140f;
	override val penetrationDamage = 1;
	override val damageInvincibilityDuration = 0.15f;

	/**
	 * 총을 쏘는 적을 생성한다.
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

		val target: LivingEntity? = this.target;
		if(target == null) return;
		rotateTo(target);

		approacher.update(delta);
		if(approacher.state == ApproachTarget.State.APPROACHED)
			selectedItem?.let {
				if(it !is Shootable) return;
				it.shoot(target.position, this);
			};
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
