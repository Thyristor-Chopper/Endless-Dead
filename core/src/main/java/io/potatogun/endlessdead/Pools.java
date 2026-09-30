package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.pools.MutablePositionPool;
import io.potatogun.gdxhelper.pools.UnorderedArrayPool;
import io.potatogun.gdxhelper.util.ArraySuppliers;

/**
 * 쓰레기 수집을 줄이기 위한 객체 풀이다.
 */
public final class Pools {
	public static final UnorderedArrayPool<Entity> entityArray = new UnorderedArrayPool<>(128, false, ArraySuppliers.entity);
	public static final MutablePositionPool position = new MutablePositionPool();

	private Pools() {
		throw new UnsupportedOperationException("this class cannot be instantiated");
	}
}
