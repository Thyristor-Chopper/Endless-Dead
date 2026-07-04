package io.potatogun.endlessdead.entity.turret;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.InventoryItemSelector;
import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Mob;
import io.potatogun.endlessdead.entity.PenetratorDamagable;
import io.potatogun.endlessdead.entity.ai.RotateToTarget;
import io.potatogun.endlessdead.entity.ai.ShootTarget;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;
import kotlin.random.Random;

/**
 * 포탑 - 자동 총알 발사 기계
 */
abstract class Turret private constructor(world: World, name: String, x: Float, y: Float, gun: Item?, health: Int, isPermanent: Boolean, texture: Texture, final override val inventory: SingleItemInventory) : Mob(world, name, x, y, 83f, 154f, health, texture), InventoryHolder, ItemSelectable by InventoryItemSelector(inventory), PenetratorDamagable {
	private val rotator = RotateToTarget(this);
	private val shooter = ShootTarget(this);
	override val movementSpeed = 0f;
	override val penetrationDamage = (health * 0.1f).toInt();
	override val damageInvincibilityDuration = 0.05f;

	/**
	 * 포탑을 생성한다.
	 *
	 * @param world       속한 세계
	 * @param name        개체 표시 이름
	 * @param x           X 좌표
	 * @param y           Y 좌표
	 * @param gun         포탑의 총
	 * @param health      포탑의 체력
	 * @param isPermanent 포탑이 영구적인지의 여부(죽지 못하는지)
	 * @param texture     개체 텍스처
	 */
	@JvmOverloads constructor(world: World, name: String, x: Float, y: Float, gun: Item?, health: Int, isPermanent: Boolean = false, texture: Texture) : this(world, name, x, y, gun, health, isPermanent, texture, SingleItemInventory());

	init {
		gun?.let {
			inventory.addItem(it);
			selectItem(it);
		};
		rotate(Random.nextInt(360).toFloat());
		if(isPermanent) isInvincible = true;
	}

	final override fun update(delta: Float) {
		super.update(delta);
		rotator.update(delta);
		shooter.update(delta);
	}
}
