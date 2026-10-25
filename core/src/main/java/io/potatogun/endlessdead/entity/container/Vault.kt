package io.potatogun.endlessdead.entity.container;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.world.World;

/**
 * 금고
 *
 * @param world       개체가 속한 세계
 * @param x           개체의 처음 X 위치
 * @param y           개체의 처음 Y 위치
 * @param initialItem 처음 들어있는 아이템
 */
open class Vault(world: World? = null, x: Float = 0f, y: Float = 0f, initialItem: Item? = null): HardContainer(world, "Vault", x, y, 34f, 32f, EndlessDead.instance.resources.textures.getShared("vault"), EndlessDead.instance.resources.textures.getShared("empty_vault"), initialItem, 5f) {
	init {
		setOriginOffsetX(-3.5f);
	}
}
