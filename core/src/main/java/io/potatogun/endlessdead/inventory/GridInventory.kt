package io.potatogun.endlessdead.inventory;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.endlessdead.ArraySuppliers;
import io.potatogun.endlessdead.item.Item;

import java.util.function.Consumer;

/**
 * 2차원 인벤토리 구현체
 *
 * @property rows    행 수
 * @property columns 열 수
 * @throws IllegalArgumentException 열이나 행 개수가 잘못된 경우
 */
class GridInventory(val rows: Int, val columns: Int) : ObservableInventory() {
	private val inventory = GdxArray<GdxArray<Item?>>(true, rows, ArraySuppliers.itemArray).apply {
		for(i in 0 until rows)
			add(GdxArray<Item?>(true, columns, ArraySuppliers.item).apply {
				for(j in 0 until columns)
					add(null);
			});
	};
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("size")
	override val size: Int
		get() {
			var ret = 0;
			for(i in 0 until rows)
				for(j in 0 until columns)
					if(inventory.items[i].items[j] != null)
						ret++;
			return ret;
		};
	override val isEmpty: Boolean
		get() {
			for(i in 0 until rows)
				for(j in 0 until columns)
					if(inventory.items[i].items[j] != null)
						return false;
			return true;
		};
	override val maxSlots = columns * rows;

	init {
		if(columns <= 0 || rows <= 0)
			throw IllegalArgumentException("invalid row or column count");
	}

	override fun addItem(item: Item): Boolean {
		if(hasItem(item)) return false;
		var emptyI = -1;
		var emptyJ = -1;
		rowloop@ for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory.items[i].items[j] == null) {
					emptyI = i;
					emptyJ = j;
					break@rowloop;
				}
		if(emptyI == -1 || emptyJ == -1) return false;
		val holder: Inventory? = item.inventory;
		if(!(holder?.removeItem(item) ?: true)) return false;  // ?: true가 있어서 기존에 들고 있던 개체가 없다면 정상 추가
		inventory.items[emptyI].items[emptyJ] = item;
		item.inventory = this;
		invokeAddObservers(item);
		return true;
	}

	/**
	 * 지정한 행과 열의 아이템을 삭제한다.
	 *
	 * @param i 행
	 * @param j 열
	 */
	fun removeItem(i: Int, j: Int): Boolean {
		if(i < 0 || i >= rows || j < 0 || j >= columns) return false;
		val item = inventory.items[i].items[j];
		if(item == null) return false;
		inventory.items[i].items[j] = null;
		item.inventory = null;
		invokeRemoveObservers(item);
		return true;
	}

	override fun removeItem(index: Int): Boolean {
		if(index < 0) return false;
		var n = 0;
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item: Item? = inventory.items[i].items[j];
				if(item != null) {
					if(n == index) {
						inventory.items[i].items[j] = null;
						item.inventory = null;
						invokeRemoveObservers(item);
						return true;
					}
					n++;
				}
			}
		return false;
	}

	override fun removeItem(item: Item): Boolean {
		for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory.items[i].items[j] === item) {
					inventory.items[i].items[j] = null;
					item.inventory = null;
					invokeRemoveObservers(item);
					return true;
				}
		return false;
	}

	/**
	 * 지정한 행과 열의 아이템을 가져온다. 없으면 null이다.
	 *
	 * @param i 행
	 * @param j 열
	 * @throws IndexOutOfBoundsException 행과 열이 범위를 벗어났을 때
	 */
	fun getItem(i: Int, j: Int): Item? {
		if(i < 0 || i >= rows || j < 0 || j >= columns) throw IndexOutOfBoundsException("column or row out of bounds");
		return inventory.items[i].items[j];
	}

	override fun getItem(index: Int): Item {
		if(index < 0) throw IndexOutOfBoundsException("index out of bounds");
		var n = 0;
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item: Item? = inventory.items[i].items[j];
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
				if(inventory.items[i].items[j] === item)
					return true;
		return false;
	}

	override fun indexOf(item: Item): Int {
		var n = 0;
		for(i in 0 until rows)
			for(j in 0 until columns)
				if(inventory.items[i].items[j] != null) {
					if(inventory.items[i].items[j] === item)
						return n;
					n++;
				}
		return -1;
	}

	override fun getItems(): GdxArray<Item> {
		val output = GdxArray<Item>(false, rows * columns);
		getItems(output);
		return output;
	}

	override fun getItems(output: GdxArray<Item>) {
		output.clear();
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item: Item? = inventory.items[i].items[j];
				if(item != null)
					output.add(item);
			}
	}

	override fun forEachItems(callback: Consumer<Item>) {
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item = inventory.items[i].items[j];
				if(item != null)
					callback.accept(item);
			}
	}

	override fun forEachItemsReverse(callback: Consumer<Item>) {
		for(i in (rows - 1) downTo 0)
			for(j in (columns - 1) downTo 0) {
				val item = inventory.items[i].items[j];
				if(item != null)
					callback.accept(item);
			}
	}

	override fun clear() {
		for(i in 0 until rows)
			for(j in 0 until columns) {
				val item = inventory.items[i].items[j];
				if(item != null) {
					inventory.items[i].items[j] = null;
					item.inventory = null;
					invokeRemoveObservers(item);
				}
			}
		invokeClearObservers();
	}

	/**
	 * 인벤토리를 행렬(2차원 배열) 형태로 사본으로 반환한다.
	 *
	 * @return 인벤토리 행렬
	 */
	fun getGrid(): GdxArray<GdxArray<Item?>> {
		val inventoryClone = GdxArray<GdxArray<Item?>>(rows);
		for(i in 0 until rows) {
			inventoryClone.add(GdxArray<Item?>(columns));
			for(j in 0 until columns)
				inventoryClone[i].add(inventory.items[i].items[j]);
		}
		return inventoryClone;
	}
}
