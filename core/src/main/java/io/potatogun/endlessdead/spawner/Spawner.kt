package io.potatogun.endlessdead.spawner;

import io.potatogun.gdxhelper.world.World;

/**
 * 개체 소환기
 *
 * @param world 소환기가 소환할 월드
 */
abstract class Spawner(@JvmField protected val world: World) {
	/**
	 * 매 프레임 실행하는 서브루틴
	 */
	abstract fun update(delta: Float);
}
