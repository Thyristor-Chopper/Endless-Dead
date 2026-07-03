package io.potatogun.endlessdead.spawner;

import io.potatogun.gdxhelper.world.World;

/**
 * 개체 소환기
 */
interface Spawner {
	/**
	 * 매 프레임 실행하는 서브루틴
	 *
	 * @param delta 직전 프레임과의 간격(초)
	 */
	fun update(delta: Float);
}
