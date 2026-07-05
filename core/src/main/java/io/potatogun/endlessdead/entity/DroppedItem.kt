package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.inventory.SingleItemInventory;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.TextureUtils;
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
	// 개체가 텍스처를 갖지 않고 아이템의 텍스처를 빌려 쓸 뿐이라 dispose하면 안 됨
	override fun dispose() {}
}
