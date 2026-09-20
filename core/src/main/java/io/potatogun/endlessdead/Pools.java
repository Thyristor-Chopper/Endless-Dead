package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.pools.MutablePositionPool;
import io.potatogun.gdxhelper.pools.UnorderedArrayPool;

/**
 * 쓰레기 수집을 줄이기 위한 객체 풀이다.
 */
public final class Pools {
	private Pools() {
		throw new UnsupportedOperationException("this class cannot be instantiated");
	}

	public static final UnorderedArrayPool<Entity> entityArray = new UnorderedArrayPool<>(128, false);
	public static final MutablePositionPool position = new MutablePositionPool();
}
