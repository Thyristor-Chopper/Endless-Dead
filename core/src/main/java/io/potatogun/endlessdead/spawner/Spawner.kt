package io.potatogun.endlessdead.spawner;

import io.potatogun.gdxhelper.world.World;

/**
 * 개체 소환기
 *
 * @param world 소환기가 소환할 월드
 */
interface Spawner {
	/**
	 * 매 프레임 실행하는 서브루틴
	 */
	fun update(delta: Float);
}
