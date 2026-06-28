package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.world.World;

/**
 * 약한 좀비
 *
 * @param world 개체가 속한 세계
 * @param x     개체의 처음 X 위치
 * @param y     개체의 처음 Y 위치
 */
class WeakZombie(world: World, x: Float, y: Float) : Zombie(world, "Small Zombie", x, y, 21f, 30f, Zombie.Properties(3, 1, 150f));
