package io.potatogun.endlessdead.item;

/**
 * 기관총
 *   allowContinuousUse을 true로 놓아 키다운으로도 연사 가능
 */
class MachineGun : Gun("machine_gun", "Machine Gun", 5, 500f, 2, true, 0.1f, 30, 30) {
	override val isContinuousUseAllowed = true;
}
