package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.util.EntityArrayPool;
import io.potatogun.gdxhelper.util.MutablePositionPool;

object Pools {
	@JvmField val entityArray = EntityArrayPool(autoClear = false);
	@JvmField val position = MutablePositionPool();
}
