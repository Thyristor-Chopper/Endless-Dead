package io.potatogun.endlessdead.entity;

/**
 * 관통하는 총알에게 영향을 주는 개체
 */
interface PenetratorDamagable {
	/**
	 * 관통할 때 관통자에게 주는 대미지
	 */
	open val penetrationDamage: Int;
}
