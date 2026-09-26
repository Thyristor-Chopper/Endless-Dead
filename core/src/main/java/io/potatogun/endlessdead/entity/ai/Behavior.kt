package io.potatogun.endlessdead.entity.ai;

import io.potatogun.gdxhelper.util.Updatable;

/**
 * 개체의 AI이다.
 */
abstract class Behavior : Updatable {
	/**
	 * 마지막 상태 갱신 결과
	 */
	var lastResult: Result? = null
		protected set;

	/**
	 * 이 프레임에서 행동한다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	abstract override fun update(delta: Float);

	/**
	 * 행동 결과
	 */
	enum class Result {
		SUCCEEDED,
		FAILED,
		REJECTED;
	}
}
