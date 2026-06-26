package io.potatogun.endlessdead.entity;

/**
 * 자신의 몸 자체가 공격수단인 개체
 */
interface BodyDamagable {
	/**
	 * 다른 개체에 닿았을 때 몸 대미지
	 */
	open val bodyDamage: Int;
	/**
	 * 동일 개체에 대해 몸 대미지 무시 여부
	 */
	open val ignoreFriendBodyDamage: Boolean;
}
