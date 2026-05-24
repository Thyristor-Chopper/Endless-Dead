package com.oop.game.entity.container;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.oop.game.entity.Entity;
import com.oop.game.entity.InventoryEntity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

/**
 * 아이템 상자 역할을 하는 추상 클래스
 *
 * @param initialItem	처음 들어있는 아이템
 */
abstract class Container(world: World, x: Float, y: Float, width: Float, height: Float, texture: String, initialItem: Item? = null) : Entity(world, x, y, width, height, texture) {
	open protected val emptyTexture: Texture? = null;
	open protected val flagTexture: Texture? = null;
	var containedItem: Item? = initialItem  // 들어있는 아이템
		protected set;
	var flag: Boolean = false
		protected set;
	val isEmpty: Boolean
		get() = (containedItem == null);
	
	override fun draw(batch: SpriteBatch) {
		val texture = this.texture;
		val flagTexture = this.flagTexture;
		val emptyTexture = this.emptyTexture;
		if(isEmpty && emptyTexture != null) {
			batch.draw(emptyTexture, x, y, width, height);
		} else if(flag && flagTexture != null) {
			batch.draw(flagTexture, x, y, width, height);
		} else if(texture != null) {
			batch.draw(texture, x, y, width, height);
		}
	}
	
	/**
	 * 아이템 가져가기
	 *
	 * @param taker	아이템을 가져가는 인벤토리를 가진 개체
	 */
	fun takeItem(taker: InventoryEntity, select: Boolean = false) {
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null) {
			// TODO 아이템이 없다는 메시지 표시
			return;
		}
		taker.addItemToInventory(target, select);
		containedItem = null;
		if(flag) flag = false;
	}
	
	/**
	 * 아이템 넣기
	 *
	 * @param item	넣을 아이템
	 */
	fun putItem(item: Item, setFlag: Boolean = false) {
		containedItem = item;
		if(setFlag) flag = true;
	}
}
