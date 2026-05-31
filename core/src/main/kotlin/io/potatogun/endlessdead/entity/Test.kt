package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.item.Fireable;
import io.potatogun.endlessdead.item.Gun;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.item.TestGun;
import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.world.World;

/**
 * 단순 테스트용
 */
class Test(world: World, position: Position) : LivingEntity(world, position, 80f, 80f, "test.bmp", 100), InventoryEntity by InventoryEntityImpl() {
	override val penetrationDamage = 1;
	override val defaultInvincibleDuration = 0.25f;
	var target: LivingEntity = world.player
		private set;
    private val distanceToTarget: Float?
        inline get() = distanceTo(target);
	private val speed: Float = 100f;

	init {
		addItemToInventory(TestGun(world), true);
	}

	override fun update(delta: Float) {
		super.update(delta);

		if(target !== world.player && !target.isAlive)
			target = world.player;

		rotateTo(target.position);

        val dx = target.position.x - position.x;
        val dy = target.position.y - position.y;
        val distance = distanceToTarget;

        if(distance != null && distance > 360f) {
            position.x += dx / distance * speed * delta;
            position.y += dy / distance * speed * delta;
        } else {
			val selected: Item? = selectedItem;
			if(selected is Fireable)
				selected.fire(target.position, this);
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
