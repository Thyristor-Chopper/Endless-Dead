package io.potatogun.endlessdead;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArraySupplier;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.spawner.Spawner;

import java.util.function.Consumer;

/**
 * libGDX 특정 자료형 배열의 생성자 모음이다. 코틀린은 ::new가 안 되므로 자바로 작성되었다.
 */
public final class ArraySuppliers {
	public static final ArraySupplier<Item[]> item = Item[]::new;
	public static final ArraySupplier<Array<Item>[]> itemArray = (ArraySupplier<Array<Item>[]>) Array[]::new;
	public static final ArraySupplier<Consumer<Item>[]> itemConsumer = (ArraySupplier<Consumer<Item>[]>) Consumer[]::new;
	public static final ArraySupplier<Spawner[]> spawner = Spawner[]::new;

	private ArraySuppliers() {
		throw new UnsupportedOperationException("this class cannot be instantiated");
	}
}
