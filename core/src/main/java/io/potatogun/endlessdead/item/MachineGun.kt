package io.potatogun.endlessdead.item;

/**
 * 기관총 아이템 구현체.
 *
 * allowContinuousUse을 true로 놓아 키다운으로도 연사 가능하다.
 */
class MachineGun : Gun("machine_gun", "Machine Gun", Gun.Properties(5, 500f).bulletPenetration(2).fireInterval(0.1f).bullets(40)) {
	override val isContinuousUseAllowed = true;
}
