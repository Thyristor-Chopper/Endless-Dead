package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.pools.EntityArrayPool;
import io.potatogun.gdxhelper.pools.MutablePositionPool;

object Pools {
	@JvmField val entityArray = EntityArrayPool(128, autoClear = false);
	@JvmField val position = MutablePositionPool();
}
