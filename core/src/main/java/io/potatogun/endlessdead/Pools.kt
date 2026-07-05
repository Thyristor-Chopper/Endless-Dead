package io.potatogun.endlessdead;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.pools.ArrayPool;
import io.potatogun.gdxhelper.pools.MutablePositionPool;

/**
 * 쓰레기 수집을 줄이기 위한 객체 풀이다.
 */
object Pools {
	@JvmField val entityArray = ArrayPool<Entity>(128, autoClear = false);
	@JvmField val position = MutablePositionPool();
}
