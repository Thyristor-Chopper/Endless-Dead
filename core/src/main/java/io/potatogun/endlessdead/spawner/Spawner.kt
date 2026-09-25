package io.potatogun.endlessdead.spawner;

import io.potatogun.gdxhelper.world.World;

/**
 * 개체 소환기
 */
abstract class Spawner {
	/**
	 * 매 프레임 실행하는 서브루틴
	 *
	 * @param delta 직전 프레임과의 간격(초)
	 */
	abstract fun update(delta: Float);
}
