package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.world.World;

/**
 * 보통 좀비
 *
 * @param world 개체가 속한 세계
 * @param x     개체의 처음 X 위치
 * @param y     개체의 처음 Y 위치
 */
class NormalZombie(world: World, x: Float, y: Float) : Zombie(world, "Zombie", x, y, 32f, 45f, Zombie.Properties(6, 3, 100f));
