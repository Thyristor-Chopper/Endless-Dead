package com.oop.game.entity.container;

import com.oop.game.Textures;
import com.oop.game.item.Item;
import com.oop.game.world.World;

/**
 * 상자
 *
 * @param world	개체가 속한 세계
 * @param x		개체 가로 위치
 * @param y		개체 세로 위치
 * @param initialItem	처음 들어있는 아이템
 */
class Chest(world: World, x: Float, y: Float, initialItem: Item? = null): Container(world, x, y, 24f, 25f, Textures.chest, Textures.emptyChest, initialItem) {
	override val playerItemTexture = Textures.chestPlayerItem;
}
