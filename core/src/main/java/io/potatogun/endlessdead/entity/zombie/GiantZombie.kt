package io.potatogun.endlessdead.entity.zombie;

import io.potatogun.gdxhelper.world.World;

/**
 * 왕좀비
 *
 * @param world 개체가 속한 세계
 * @param x     개체의 처음 X 위치
 * @param y     개체의 처음 Y 위치
 */
class GiantZombie(world: World, x: Float, y: Float) : Zombie(world, "Giant Zombie", x, y, 100f, 140f, Zombie.Properties(100, 15, 10f));
