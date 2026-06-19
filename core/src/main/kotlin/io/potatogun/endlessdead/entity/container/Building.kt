package io.potatogun.endlessdead.entity.container;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.world.World;

/**
 * 건물
 *
 * @param world			개체가 속한 세계
 * @param x				개체의 처음 X 위치
 * @param y				개체의 처음 Y 위치
 * @param initialItem	처음 들어있는 아이템
 */
class Building(world: World, x: Float, y: Float, initialItem: Item? = null): Container(world, x, y, 23f, 24f, Textures.getShared("building"), Textures.getShared("empty_building"), initialItem) {
	override val playerItemTexture = Textures.getShared("building_player_item");

	/**
	 * 공유 자원이기 때문에 여기서 정리하지 않고 다른 인스턴스에서 재활용한다.
	 */
	override fun dispose() {
		containedItem?.destroy();
	}
}
