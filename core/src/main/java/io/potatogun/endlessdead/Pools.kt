package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.pools.EntityArrayPool;
import io.potatogun.gdxhelper.pools.MutablePositionPool;

/**
 * 쓰레기 수집을 줄이기 위한 객체 풀이다.
 */
object Pools {
	@JvmField val entityArray = EntityArrayPool(128, autoClear = false);
	@JvmField val position = MutablePositionPool();
}
