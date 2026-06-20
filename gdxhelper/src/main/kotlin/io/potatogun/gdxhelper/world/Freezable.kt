package io.potatogun.gdxhelper.world;

/**
 * 시간 멈춤이 가능한 월드
 */
interface Freezable {
	/**
	 * 현재 시간이 멈춰있는지 여부
	 * 
	 * 자바 개발자에 대한 배려로 getIsFrozen이 되지 않을까 생각했지만
	 *   디컴파일해서 자바 코드를 본 결과 isFrozen() getter 함수로 되었다.
	 *   하지만 is로 시작하지 않는 경우엔 여전히 get가 붙는 것까지 확인했다.
	 */
	val isFrozen: Boolean;

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
