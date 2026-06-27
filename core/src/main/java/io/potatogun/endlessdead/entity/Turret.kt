package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Shootable;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;
import kotlin.random.Random;

/**
 * 포탑 (자동 총알 발사 기계)
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
abstract class Turret @JvmOverloads constructor(world: World, name: String, x: Float, y: Float, gun: Item?, health: Int, isPermanent: Boolean = false, texture: Texture) : LivingEntity(world, name, x, y, 83f, 154f, health, texture), InventoryHolder, AttackTargetable, PenetratorDamagable {
	private val targeter = AutoTargeter(this);  // 클래스 정의 시 위임자에게 this만 넘길 수 있었어도 이딴 수동 위임같은 뻘짓 안 나오지...
	final override val inventory = SingleItemInventory();
	override var target: LivingEntity?
		get() = targeter.target
		set(value) { targeter.target = value };
	override val penetrationDamage = (health * 0.1f).toInt();
	override val defaultInvincibleDuration = 0.05f;

	init {
		gun?.let { inventory.addItem(it) };
		rotate(Random.nextInt(360).toFloat());
		if(isPermanent) isInvincible = true;
	}

	protected fun setTargetFetcher(fetcher: () -> LivingEntity?) {
		targeter.setTargetFetcher(fetcher);
	}

	protected fun setFollowRange(range: Float) {
		targeter.setFollowRange(range);
	}

	final override fun update(delta: Float) {
		super.update(delta);

		val target: LivingEntity? = this.target;
		if(target == null) return;
		rotateTo(target);

		inventory.getItem()?.let {
			if(it !is Shootable) return;
			it.shoot(target.position, this);
		};
	}
}
