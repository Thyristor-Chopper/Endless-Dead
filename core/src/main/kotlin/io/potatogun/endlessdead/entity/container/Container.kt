package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.world.World;

/**
 * 아이템 상자 역할을 하는 추상 클래스
 *
 * @param world			개체가 속한 세계
 * @param position		개체의 처음 위치
 * @param width			개체 너비
 * @param height		개체 높이
 * @param texture		개체 텍스처
 * @param emptyTexture	상자가 비어 있을 때 사용할 텍스처
 * @param initialItem	처음 들어있는 아이템
 */
abstract class Container(world: World, position: Position, width: Float, height: Float, texture: Texture?, private val emptyTexture: Texture? = null, initialItem: Item? = null) : Entity(world, position, width, height, texture) {
	// 플레이어가 직접 아이템을 넣었을 때의 텍스처
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
	 * 상자가 비어 있는지의 여부
	 */
	val isEmpty: Boolean
		get() = (containedItem == null);

	/**
	 * 상자를 화면에 그린다. 비어 있을 때와 아닐 때 텍스처가 다르기 때문에 override해서 처리한다.
	 */
	override fun draw(batch: SpriteBatch) {
		val texture: Texture? = 
			if(isEmpty) emptyTexture
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
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null) return null;
		taker.addItemToInventory(target, select);
		if(isPlayerItem) isPlayerItem = false;
		return target;
	}

	/**
	 * 아이템 넣기
	 *
	 * @param item	넣을 아이템
	 */
	@JvmOverloads fun putItem(item: Item, isPlayerItem: Boolean = false) {
		if(!isEmpty) throw IllegalStateException("container is not empty");
		containedItem = item;
		val holder: Entity? = item.holder;
		if(holder is InventoryEntity)
			holder.removeItemFromInventory(item);
		if(isPlayerItem) this.isPlayerItem = true;
	}

	/**
	 * 안에 들어 있는 아이템을 제거한다.
	 *
	 * @return 성공 여부
	 */
	fun removeItem(): Boolean {
		if(containedItem == null) return false;
		containedItem = null;
		if(isPlayerItem) isPlayerItem = false;
		return true;
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
