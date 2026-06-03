package io.potatogun.gdxhelper.world;

/**
 * 시간 멈춤이 가능한 월드
 */
interface Freezable {
	/**
	 * 현재 시간이 멈춰있는지 여부
	 * 
	 * val isFrozen: Boolean;으로 하지 않은 이유는 자바 개발자에 대한 배려이다.
	 * 인터페이스의 필드는 JvmName이 불가능하다. 이렇게 하면 getIsFrozen()라는 
	 * getter가 코틀린 컴파일러에 의해 생성되는데 굉장히 어색한 이름이다.
	 */
	fun isFrozen(): Boolean;

	/**
	 * 시간을 멈춘다.
	 *
	 * @param duration 멈춤 지속 시간(초)
	 */
	fun freeze(duration: Float);

	/**
	 * 시간을 다시 흐르게 한다.
	 */
	fun unfreeze();
}
