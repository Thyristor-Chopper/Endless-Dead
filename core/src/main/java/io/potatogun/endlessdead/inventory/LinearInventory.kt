package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 인벤토리의 기본적인 구현체
 *
 * @property maxSlots 최대 아이템 개수(-1: 무제한)
 * @throws IllegalArgumentException 최대 아이템 개수가 잘못된 경우
 */
class LinearInventory(override val maxSlots: Int = -1) : ObservableInventory() {
	private val inventory = GdxArray<Item>();
	override val size: Int
		get() = inventory.size;
	override val isEmpty: Boolean
		get() = inventory.isEmpty();

	init {
		if(maxSlots < -1)
			throw IllegalArgumentException("maxSlots must be positive, zero or -1");
	}

	override fun addItem(item: Item): Boolean {
		if(maxSlots != -1 && inventory.size >= maxSlots) return false;
		if(hasItem(item)) return false;
		val holder: Inventory? = item.inventory;
		if(!(holder?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가
		inventory.add(item);
		item.inventory = this;
		invokeItemAddObservers(item);
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		if(index < 0 || index >= inventory.size) return false;
		val item = inventory[index];
		inventory.removeIndex(index);
		item.inventory = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun removeItem(item: Item): Boolean {
		if(!inventory.removeValue(item, true)) return false;
		item.inventory = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun getItem(index: Int): Item = inventory[index];

	override fun hasItem(item: Item): Boolean = inventory.contains(item, true);

	override fun indexOf(item: Item): Int = inventory.indexOf(item, true);

	override fun getItems(): GdxArray<Item> {
		val output = GdxArray<Item>(inventory.size);
		getItems(output);
		return output;
	}

	override fun getItems(output: GdxArray<Item>) {
		output.clear();
		for(i in 0 until inventory.size)
			output.add(inventory[i]);
	}

	override fun forEachItems(callback: Consumer<Item>) {
		for(i in 0 until inventory.size)
			callback.accept(inventory[i]);
	}

	override fun forEachItemsReverse(callback: Consumer<Item>) {
		for(i in (inventory.size - 1) downTo 0)
			callback.accept(inventory[i]);
	}

	override fun clear() {
		for(i in 0 until inventory.size)
			inventory[i].inventory = null;
		inventory.clear();
		invokeClearObservers();
	}
}
