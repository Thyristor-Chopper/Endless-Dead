package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

/**
 * 아이템 상자 역할을 하는 추상 클래스
 *
 * @param    world        개체가 속한 세계
 * @param    name         개체 표시 이름
 * @param    x            개체의 처음 X 위치
 * @param    y            개체의 처음 Y 위치
 * @param    width        개체 너비
 * @param    height       개체 높이
 * @param    texture      개체 텍스처
 * @property emptyTexture 상자가 비어 있을 때 사용할 텍스처
 * @param    initialItem  처음 들어있는 아이템
 */
abstract class Container(world: World, name: String, x: Float, y: Float, width: Float, height: Float, texture: Texture?, private val emptyTexture: Texture? = null, initialItem: Item? = null) : Entity(world, name, x, y, width, height, texture), InventoryHolder {
	override val inventory = SingleItemInventory();
	/**
	 * 플레이어가 직접 아이템을 넣었을 때의 텍스처
	 */
	protected open val playerItemTexture: Texture? = null;
	/**
	 * 현재 들어있는 아이템
	 */
	val containedItem: Item?
		inline get() = inventory.getItem();
	/**
	 * 플레이어가 직접 아이템을 넣었는지의 여부
	 */
	var isPlayerItem = false
		private set;

	init {
		initialItem?.let { inventory.addItem(it) };

		inventory.addItemRemoveObserver {
			if(isPlayerItem)
				isPlayerItem = false;
		};
	}

	// 상자를 화면에 그린다. 비어 있을 때와 아닐 때 텍스처가 다르기 때문에 override해서 처리한다.
	override fun draw(batch: SpriteBatch) {
		val textureOverride: Texture? = 
			if(inventory.isEmpty) emptyTexture
			else if(isPlayerItem) playerItemTexture
			else null;
		super.draw(batch, textureOverride, null);
	}

	/**
	 * 아이템 가져가기
	 *
	 * @param taker 아이템을 가져가는 인벤토리를 가진 개체
	 * @return      성공하면 들어있는 아이템, 실패하면 null
	 * @throws IllegalArgumentException	taker가 자기 자신인 경우
	 */
	fun takeItem(taker: InventoryHolder): Item? {
		if(taker === this) throw IllegalArgumentException("taker is the container itself");
		val item = inventory.getItem();  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(item == null) return null;
		if(!taker.inventory.addItem(item)) return null;
		isPlayerItem = false;
		return item;
	}

	/**
	 * 아이템 넣기
	 *
	 * @param item         넣을 아이템
	 * @param isPlayerItem 플레이어가 직접 아이템을 넣었는지의 여부
	 * @return             성공 여부
	 */
	@JvmOverloads fun putItem(item: Item, isPlayerItem: Boolean = false): Boolean {
		if(inventory.addItem(item)) {
			if(isPlayerItem) this.isPlayerItem = true;
			return true;
		}
		return false;
	}

	// 추가적인 두 텍스처도 비운다.
	override fun dispose() {
		super.dispose();
		playerItemTexture?.let { Utils.safeDispose(it) };
		emptyTexture?.let { Utils.safeDispose(it) };
	}
}
