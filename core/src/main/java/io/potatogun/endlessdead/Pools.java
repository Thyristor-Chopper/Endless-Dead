package io.potatogun.endlessdead;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.pools.ArrayPool;
import io.potatogun.gdxhelper.pools.MutablePositionPool;
import io.potatogun.gdxhelper.pools.UnorderedArrayPool;

/**
 * 쓰레기 수집을 줄이기 위한 객체 풀이다.
 */
public final class Pools {
	public static final UnorderedArrayPool<Entity> entityArray = new UnorderedArrayPool<>(128, false, io.potatogun.gdxhelper.util.ArraySuppliers.entity);
	public static final ArrayPool<Item> itemArray = new ArrayPool<>(128, false, ArraySuppliers.item);
	public static final MutablePositionPool position = new MutablePositionPool();

	private Pools() {
		throw new UnsupportedOperationException("this class cannot be instantiated");
	}
}
