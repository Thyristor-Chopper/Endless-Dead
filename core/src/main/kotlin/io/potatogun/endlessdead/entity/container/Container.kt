package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

/**
 * 아이템 상자 역할을 하는 추상 클래스
 *
 * @param world			개체가 속한 세계
 * @param x				개체의 처음 X 위치
 * @param y				개체의 처음 Y 위치
 * @param width			개체 너비
 * @param height		개체 높이
 * @param texture		개체 텍스처
 * @param emptyTexture	상자가 비어 있을 때 사용할 텍스처
 * @param initialItem	처음 들어있는 아이템
 */
abstract class Container(world: World, x: Float, y: Float, width: Float, height: Float, texture: Texture?, private val emptyTexture: Texture? = null, initialItem: Item? = null) : Entity(world, x, y, width, height, texture), InventoryEntity {
	// 상자는 아이템을 '선택'할 수 없기 때문에 이들은 null이다.
	override val selectedItem: Item? = null;
	override val selectedItemIndex: Int? = null;
	override val itemCount: Int
		get() = if(containedItem != null) 1 else 0;
	override val isInventoryEmpty: Boolean
		get() = (containedItem == null);
	override val maxSlots = 1;
	override val firstItem: Item?
		get() = containedItem;
	override val lastItem: Item?
		get() = containedItem;
	/**
	 * 플레이어가 직접 아이템을 넣었을 때의 텍스처
	 */
	open protected val playerItemTexture: Texture? = null;
	/**
	 * 들어있는 아이템
	 */
	var containedItem: Item? = initialItem
		private set;
	/**
	 * 플레이어가 직접 아이템을 넣었는지의 여부
	 */
	var isPlayerItem = false
		private set;

	/**
	 * 상자를 화면에 그린다. 비어 있을 때와 아닐 때 텍스처가 다르기 때문에 override해서 처리한다.
	 */
	override fun draw(batch: SpriteBatch) {
		val texture: Texture? = 
			if(isInventoryEmpty) emptyTexture
			else if(isPlayerItem) playerItemTexture
			else this.texture;
		super.draw(batch, texture);
	}

	/**
	 * 아이템 가져가기
	 *
	 * @param 	taker	아이템을 가져가는 인벤토리를 가진 개체
	 * @param	select	아이템을 가져간 후 자동으로 선택할지 여부
	 * @return 	성공하면 들어있는 아이템, 실패하면 null
	 */
	@JvmOverloads fun takeItem(taker: InventoryEntity, select: Boolean = false): Item? {
		if(taker === this) throw IllegalArgumentException("taker is the container itself");
		val item = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(item == null) return null;
		if(!taker.addItem(item, select)) return null;
		isPlayerItem = false;
		return item;
	}

	/**
	 * 아이템 제거
	 */
	fun removeItem(): Boolean {
		if(containedItem == null) return false;
		containedItem = null;
		isPlayerItem = false;
		return true;
	}

	// InventoryEntity 구현

	override fun addItem(item: Item, select: Boolean): Boolean {
		if(select) throw IllegalArgumentException("containers cannot select an item");
		if(containedItem != null) return false;
		val holder: Entity? = item.holder;
		if(holder === this) return false;
		containedItem = item;
		if(holder is InventoryEntity) {
			holder.removeItem(item);
			if(holder is Player) isPlayerItem = true;
		}
		return true;
	}

	override fun removeItem(index: Int) {
		if(index != 0 || !removeItem())
			throw IllegalArgumentException("index out of bounds");
	}

	override fun removeItem(item: Item): Boolean {
		if(containedItem !== item) return false;
		return removeItem();
	}

	override fun getItem(index: Int): Item {
		val item: Item? = containedItem;
		if(index != 0 || item == null) throw IllegalArgumentException("index out of bounds");
		return item;
	}

	// 상자는 아이템을 '선택'할 수 없다.
	override fun selectNextItem(): Boolean = false;

	override fun selectPreviousItem(): Boolean = false;

	override fun selectItem(item: Item): Boolean = false;

	override fun selectItem(index: Int) {
		throw UnsupportedOperationException("containers cannot select an item");
	}

	override fun hasItem(item: Item): Boolean = (containedItem === item);

	override fun getInventory(): List<Item> = containedItem?.let { listOf<Item>(it) } ?: listOf<Item>();

	override fun clearInventory() {
		containedItem?.destroy();
	}

	/**
	 * 추가적인 두 텍스처도 비운다.
	 */
	override fun dispose() {
		super.dispose();
		playerItemTexture?.dispose();
		emptyTexture?.dispose();
		containedItem?.destroy();
	}
}
