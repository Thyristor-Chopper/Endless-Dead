package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.ArraySuppliers;
import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 인벤토리의 기본적인 구현체
 *
 * @property maxSlots 최대 아이템 개수(-1: 무제한)
 * @throws IllegalArgumentException 최대 아이템 개수가 잘못된 경우
 */
class LinearInventory @JvmOverloads constructor(override val maxSlots: Int = -1) : ObservableInventory() {
	private val inventory = GdxArray<Item>(true, if(maxSlots >= 0) maxSlots else 128, ArraySuppliers.item);
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("size")
	override val size: Int by inventory::size;
	override val isEmpty: Boolean
		get() = (inventory.size == 0);

	init {
		if(maxSlots < -1)
			throw IllegalArgumentException("maxSlots must be positive, zero or -1");
	}

	override fun addItem(item: Item): Boolean {
		if(maxSlots != -1 && inventory.size >= maxSlots) return false;
		if(hasItem(item)) return false;
		val holder: Inventory? = item.inventory;
		if(!(holder?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가

		// 무한 보관함이 꽉 찼으면 두 배로 늘린다. (128개 이후 추가할 때마다 매번 배열을 새로 만드는 오버헤드 줄이기)
		if(maxSlots == -1) {
			val maxSize = inventory.items.size;
			if(maxSize == inventory.size)
				inventory.ensureCapacity(maxSize);  // 원래 (maxSize * 2 - maxSize)임
		}

		inventory.add(item);
		item.inventory = this;
		invokeAddObservers(item);
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		if(index < 0 || index >= inventory.size) return false;
		val item = inventory.items[index];
		inventory.removeIndex(index);
		item.inventory = null;
		invokeRemoveObservers(item);
		return true;
	}

	override fun removeItem(item: Item): Boolean {
		if(!inventory.removeValue(item, true)) return false;
		item.inventory = null;
		invokeRemoveObservers(item);
		return true;
	}

	override fun getItem(index: Int): Item {
		if(index < 0 || index >= inventory.size)
			throw IndexOutOfBoundsException("index out of bounds");
		return inventory.items[index];
	}

	override fun hasItem(item: Item): Boolean = inventory.contains(item, true);

	override fun indexOf(item: Item): Int = inventory.indexOf(item, true);

	override fun getItems(): GdxArray<Item> {
		val output = GdxArray<Item>(false, inventory.size, ArraySuppliers.item);
		getItems(output);
		return output;
	}

	override fun getItems(output: GdxArray<Item>) {
		output.clear();
		for(i in 0 until inventory.size)
			output.add(inventory.items[i]);
	}

	override fun forEachItems(callback: Consumer<Item>) {
		for(i in 0 until inventory.size)
			callback.accept(inventory.items[i]);
	}

	override fun forEachItemsReverse(callback: Consumer<Item>) {
		for(i in (inventory.size - 1) downTo 0)
			callback.accept(inventory.items[i]);
	}

	override fun clear() {
		for(i in 0 until inventory.size) {
			inventory.items[i].inventory = null;
			invokeRemoveObservers(inventory.items[i]);
		}
		inventory.clear();
		invokeClearObservers();
	}
}
