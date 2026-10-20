package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.entity.rotateToRandom;
import io.potatogun.gdxhelper.world.World;

/**
 * 땅에 떨어져 있는 아이템
 *
 * @param    world 개체가 속한 세계
 * @param    x     개체의 X 위치
 * @param    y     개체의 Y 위치
 * @property item  아이템
 */
class DroppedItem(world: World, x: Float, y: Float, val item: Item) : Entity(world, item.name, x, y, Constants.ITEM_SIZE, Constants.ITEM_SIZE, item.texture) {
	init {
		rotateToRandom();
	}

	/**
	 * 아이템을 줍는다.
	 * 
	 * @param collector 줍는 개체
	 * @return 성공 여부
	 */
	fun pickup(collector: InventoryHolder): Boolean {
		val world = level;
		val added = collector.inventory.addItem(item);
		if(added && world != null)
			world.entities.remove(this);  // this.remove()로 하면 dispose돼서 안 됨.
		return added;
	}

	// 개체가 텍스처를 갖지 않고 아이템의 텍스처를 빌려 쓸 뿐이라 dispose하면 안 됨
	override fun dispose() {
		if(item is Disposable)
			item.dispose();
	}
}
