package com.oop.game.entity.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.oop.game.entity.Entity;
import com.oop.game.entity.InventoryEntity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

/**
 * ������ ���� ������ �ϴ� �߻� Ŭ����
 *
 * @param initialItem	ó�� ����ִ� ������
 */
abstract class  Container(world: World, x: Float, y: Float, width: Float, height: Float, texture: String, emptyTexture: String? = null, initialItem: Item? = null) : Entity(world, x, y, width, height, texture) {
	open protected val emptyTexture: Texture? = emptyTexture?.let { Texture(Gdx.files.internal(it)) };
	open protected val playerItemTexture: Texture? = null;
	var containedItem: Item? = initialItem  // ����ִ� ������
		private set;
	var isPlayerItem = false
		private set;
	val isEmpty: Boolean
		get() = (containedItem == null);
	
	/**
	 * ���ڸ� ȭ�鿡 �׸���. ��� ���� ���� �ƴ� �� �ؽ�ó�� �ٸ��� ������ override�ؼ� ó���Ѵ�.
	 */
	override fun draw(batch: SpriteBatch) {
		val texture: Texture? = 
			if(isEmpty) emptyTexture
			else if(isPlayerItem) playerItemTexture
			else this.texture;
		super.draw(batch, texture);
	}
	
	/**
	 * ������ ��������
	 *
	 * @param 	taker	�������� �������� �κ��丮�� ���� ��ü
	 * @param	select	�������� ������ �� �ڵ����� �������� ����
	 * @return 	�����ϸ� ����ִ� ������, �����ϸ� null
	 */
	fun takeItem(taker: InventoryEntity, select: Boolean = false): Item? {
		val target = containedItem;  // https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
		if(target == null) return null;
		taker.addItemToInventory(target, select);
		containedItem = null;
		if(isPlayerItem) isPlayerItem = false;
		return target;
	}
	
	/**
	 * ������ �ֱ�
	 *
	 * @param item	���� ������
	 */
	fun putItem(item: Item, isPlayerItem: Boolean = false) {
		if(!isEmpty) throw IllegalStateException("container is not empty");
		containedItem = item;
		if(isPlayerItem) this.isPlayerItem = true;
	}
	
	/**
	 * �ȿ� ��� �ִ� �������� �����Ѵ�.
	 *
	 * @return ���� ����
	 */
	fun removeItem(): Boolean {
		if(containedItem == null) return false;
		containedItem = null;
		if(isPlayerItem) isPlayerItem = false;
		return true;
	}
	
	override fun dispose() {
		super.dispose();
		playerItemTexture?.let { it.dispose() };
		emptyTexture?.let { it.dispose() };
	}
}
