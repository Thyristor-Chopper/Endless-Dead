package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.world.World;

/**
 * 상자
 *
 * @param world			개체가 속한 세계
 * @param x				개체 가로 위치
 * @param y				개체 세로 위치
 * @param initialItem	처음 들어있는 아이템
 */
class Chest(world: World, x: Float, y: Float, initialItem: Item? = null): Container(world, x, y, 24f, 25f, "chest.bmp", "chest_empty.bmp", initialItem) {
	override val playerItemTexture = Utils.loadTexture("chest_player_added.bmp");
}
