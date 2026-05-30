package io.potatogun.endlessdead.spawner;

import io.potatogun.endlessdead.world.World;

/**
 * 개체 소환기
 */
abstract class Spawner(val world: World) {
	/**
	 * 매 프레임 실행하는 서브루틴
	 */
	internal abstract fun tick(delta: Float);

	/**
	 * 자원 정리
	 */
	internal open fun cleanUp() {}
}
