package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.world.World;

/**
 * 건물
 *
 * @param world			개체가 속한 세계
 * @param position		개체의 처음 위치
 * @param initialItem	처음 들어있는 아이템
 */
class Building(world: World, position: Position, initialItem: Item? = null): Container(world, position, 23f, 24f, "building.bmp", "building_empty.bmp", initialItem) {
	override val playerItemTexture = Utils.loadTexture("building_player_added.bmp");
}
