package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.pools.ArrayPool;
import io.potatogun.gdxhelper.pools.MutablePositionPool;

/**
 * 쓰레기 수집을 줄이기 위한 객체 풀이다.
 */
public final class Pools {
	private Pools() {
		throw new UnsupportedOperationException("this class cannot be instantiated");
	}

	public static final ArrayPool<Entity> entityArray = new ArrayPool<>(128, false);
	public static final MutablePositionPool position = new MutablePositionPool();
}
