package io.potatogun.endlessdead.entity.ai;

import io.potatogun.gdxhelper.util.Updatable;

/**
 * 개체의 AI이다.
 */
interface Behavior : Updatable {
	/**
	 * 이 프레임에서 행동한다.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	override fun update(delta: Float);
}
