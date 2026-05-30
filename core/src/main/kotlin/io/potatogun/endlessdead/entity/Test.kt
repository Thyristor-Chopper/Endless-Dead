package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.TestGun;
import io.potatogun.endlessdead.world.World;

import kotlin.math.sqrt;

/**
 * 단순 테스트용
 */
class Test(world: World, x: Float, y: Float, private val speed: Float = 100f) : LivingEntity(world, x, y, 80f, 80f, "test.bmp", 100), InventoryEntity by InventoryEntityImpl(), HostileEntity {
	override val penetrationDamage = 1;
	override val defaultInvincibleDuration = 0.25f;
	override var target: LivingEntity? = world.player
		private set;
    private val distanceToTarget: Float?
        get() = target?.position?.distanceTo(position);

	init {
		addItemToInventory(TestGun(world), true);
	}

	override fun update(delta: Float) {
		super.update(delta);
		
		val target: LivingEntity? = this.target;
		if(target == null)
			return;
		if(!target.isAlive) {
			this.target = null;
			return;
		}
		
        val dx = target.x - x;
        val dy = target.y - y;
        val distance = distanceToTarget;
		
        if(distance != null && distance > 320f) {
            x += dx / distance * speed * delta;
            y += dy / distance * speed * delta;
        } else {
			val selected: Item? = selectedItem;
			if(selected is Gun)
				selected.use();
		}
    }
	
	override fun onDamage(damage: Int, attacker: Entity?) {
		if(attacker is LivingEntity)
			target = attacker;
	}
	
	override fun onItemDestoryed(item: Item) {
		if(item is Gun)
			addItemToInventory(TestGun(world), true);
	}
}
