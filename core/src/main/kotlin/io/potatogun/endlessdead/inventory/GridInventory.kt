package io.potatogun.endlessdead.inventory;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;

/**
 * 2차원 인벤토리 구현체
 *
 * @property rows    행 수
 * @property columns 열 수
 * @throws IllegalArgumentException 열이나 행 개수가 잘못된 경우
 */
class GridInventory(val rows: Int, val columns: Int) : ObservableInventory() {
	private val inventory: Array<Array<Item?>> = Array(rows) { arrayOfNulls<Item>(columns) };
	override val itemCount: Int
		get() {
			var ret = 0;
			for(i in 0 until rows)
				for(j in 0 until columns)
					if(inventory[i][j] != null)
						ret++;
			return ret;
		}
	override val isEmpty: Boolean
		get() {
			for(i in 0 until rows)
				for(j in 0 until columns)
					if(inventory[i][j] != null)
						return false;
			return true;
		}
	override val firstItem: Item?
		get() {
			for(i in 0 until rows)
				for(j in 0 until columns)
					if(inventory[i][j] != null)
						return inventory[i][j];
			return null;
		}
	override val lastItem: Item?
		get() {
			for(i in (rows - 1) downTo 0)
				for(j in (columns - 1) downTo 0)
					if(inventory[i][j] != null)
						return inventory[i][j];
			return null;
		}
	override val maxSlots = columns * rows;

	init {
		if(columns <= 0 || rows <= 0)
			throw IllegalArgumentException("invalid row or column count");
	}

	override fun addItem(item: Item): Boolean {
		if(hasItem(item)) return false;
		var emptyI = -1;
		var emptyJ = -1;
		for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory[i][j] == null) {
					emptyI = i;
					emptyJ = j;
					break;
				}
		if(emptyI == -1 || emptyJ == -1) return false;
		val holder: InventoryHolder? = item.holder;
		if(!(holder?.inventory?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가
		inventory[emptyI][emptyJ] = item;
		invokeItemAddObservers(item, holder);
		return true;
	}

	fun removeItem(i: Int, j: Int): Boolean {
		if(i < 0 || i >= rows || j < 0 || j >= columns) return false;
		val item = inventory[i][j];
		if(item == null) return false;
		inventory[i][j] = null;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		if(index < 0) return false;
		var n = 0;
		var removed = false;
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item: Item? = inventory[i][j];
				if(item != null) {
					if(n == index) {
						inventory[i][j] = null;
						invokeItemRemoveObservers(item);
						removed = true;
						break;
					}
					n++;
				}
			}
		return removed;
	}

	override fun removeItem(item: Item): Boolean {
		var removed = false;
		for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory[i][j] === item) {
					inventory[i][j] = null;
					removed = true;
					break;
				}
		if(!removed) return false;
		invokeItemRemoveObservers(item);
		return true;
	}

	override fun getItem(index: Int): Item {
		if(index < 0) throw IndexOutOfBoundsException("index out of bounds");
		var n = 0;
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item: Item? = inventory[i][j];
				if(item != null) {
					if(n == index)
						return item;
					n++;
				}
			}
		throw IndexOutOfBoundsException("index out of bounds");
	}

	override fun hasItem(item: Item): Boolean {
		for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory[i][j] === item)
					return true;
		return false;
	}

	override fun indexOf(item: Item): Int {
		var n = 0;
		for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory[i][j] != null) {
					if(inventory[i][j] === item)
						return n;
					n++;
				}
		return -1;
	}

	override fun getItems(): List<Item> {
		val ret = mutableListOf<Item>();
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item: Item? = inventory[i][j];
				if(item != null)
					ret.add(item);
			}
		return ret.toList();
	}

	override fun clear() {
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item = inventory[i][j];
				if(item != null) {
					inventory[i][j] = null;
					invokeItemRemoveObservers(item);
				}
			}
		invokeClearObservers();
	}

	/**
	 * 인벤토리를 행렬(2차원 배열) 형태로 사본으로 반환한다.
	 *
	 * @return 인벤토리
	 */
	fun getGrid(): Array<Array<Item?>> {
		val inventoryClone: Array<Array<Item?>> = Array(rows) { arrayOfNulls<Item>(columns) };
		for(i in 0 until rows)
			for(j in 0 until columns)
				inventoryClone[i][j] = inventory[i][j];
		return inventoryClone;
	}
}
