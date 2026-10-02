package io.potatogun.endlessdead.world;

import io.potatogun.gdxhelper.world.World;

/**
 * 다른 월드에 종속돼있는 특수 월드
 */
interface SpecialWorld {
	val mainWorld: World;
}
