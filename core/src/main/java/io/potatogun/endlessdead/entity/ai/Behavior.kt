package io.potatogun.endlessdead.entity.ai;

/**
 * 개체의 AI이다.
 */
interface Behavior {
	/**
	 * 이 프레임에서 행동한다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 * @return      이 프레임에서의 행동 결과
	 */
	fun update(delta: Float): Result;

	/**
	 * 행동 결과
	 */
	enum class Result {
		SUCCEEDED,
		FAILED,
		REJECTED;
	}
}
