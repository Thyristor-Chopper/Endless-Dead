package io.potatogun.endlessdead.entity;

/**
 * 프레임당 일정한 속도를 갖고 움직이는 개체
 */
interface Movable {
	/**
	 * 개체의 이동 속도
	 */
	val speed: Float;

	/**
	 * 이번 프레임에서 지정한 방향으로 이동한다.
	 *
	 * @param delta      직전 프레임과의 간격(초)
	 * @param directionX 가로 방향 단위 벡타
	 * @param directionY 세로 방향 단위 벡타
	 */
	fun move(delta: Float, directionX: Float, directionY: Float);
}
