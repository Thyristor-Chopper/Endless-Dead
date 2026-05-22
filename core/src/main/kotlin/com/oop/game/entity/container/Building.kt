package com.oop.game.entity.container;

import com.oop.game.world.World;

/**
 * 건물
 *
 * @param world	개체가 속한 세계
 * @param x		개체 가로 위치
 * @param y		개체 세로 위치
 */
class Building(world: World, x: Float, y: Float): Container(world, x, y, 32.0f, 32.0f, "building.png");
