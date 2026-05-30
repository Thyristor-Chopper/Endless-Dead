package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.world.World;

/**
 * 기관총
 */
class MachineGun(world: World) : Gun(world, "machine_gun", "Machine Gun", 5, 500f, 2, true, 0.1f, 30, 30) {
	override val allowContinuousFire = true;
}
