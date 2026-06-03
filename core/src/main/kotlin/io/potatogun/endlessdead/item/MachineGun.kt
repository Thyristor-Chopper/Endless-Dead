package io.potatogun.endlessdead.item;

import io.potatogun.gdxhelper.world.World;

/**
 * 기관총
 *
 * 기관총은 allowContinuousUse을 true로 놓아 키다운으로도 연사 가능
 */
class MachineGun(world: World) : Gun(world, "machine_gun", "Machine Gun", 5, 500f, 2, true, 0.1f, 30, 30) {
	override val isContinuousUseAllowed = true;
}
