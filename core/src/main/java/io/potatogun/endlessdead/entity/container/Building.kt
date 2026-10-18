package io.potatogun.endlessdead.entity.container;

import io.potatogun.endlessdead.Resources;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.world.World;

/**
 * 건물
 *
 * @param world       개체가 속한 세계
 * @param x           개체의 처음 X 위치
 * @param y           개체의 처음 Y 위치
 * @param initialItem 처음 들어있는 아이템
 */
open class Building(world: World? = null, x: Float = 0f, y: Float = 0f, initialItem: Item? = null): Container(world, "Building", x, y, 23f, 24f, Resources.textures.getShared("building"), Resources.textures.getShared("empty_building"), initialItem) {
	override val playerItemTexture = Resources.textures.getShared("building_player_item");
}
