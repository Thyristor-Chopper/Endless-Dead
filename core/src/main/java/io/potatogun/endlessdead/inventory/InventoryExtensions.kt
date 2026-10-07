@file:JvmName("InventoryUtils")
package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.Pools;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.pools.use;

import java.util.function.Consumer;

/**
 * 인벤토리 내 각 아이템을 순회한다. (코틀린 전용)
 *
 * 원래는 이 코드를 직접 쓸 곳에 작성했기 때문에 인라인이다. (물론 람다 오버헤드를 줄이기 위한 것도 있음 헷)
 *
 * @param callback 실행할 서브루틴
 */
inline fun Inventory.forEach(callback: (Item) -> Unit) {
	Pools.itemArray.use(size) { items ->
		getItems(items);
		for(i in 0 until items.size) {
			val item = items[i];
			callback(item);
		}
	};
}

/**
 * 인벤토리 내 각 아이템을 순회한다. (자바 전용)
 *
 * 람다가 인라인되지 못하고 쓸 데 없이 변수 캡처 등이 발생할 수 있으므로 사용을 권장하지 않는다.
 *
 * @param callback 실행할 서브루틴
 */
@Deprecated(message = "using this function is discouraged due to lambda overhead such as variable capturing", level = DeprecationLevel.WARNING)
@SinceKotlin("9999.9")
fun Inventory.forEach(callback: Consumer<Item>) {
	forEach(callback::accept);
}
