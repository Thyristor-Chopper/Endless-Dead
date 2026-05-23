package com.oop.game.entity.container;

import com.oop.game.world.World;

/**
 * 상자
 *
 * @param world	개체가 속한 세계
 * @param x		개체 가로 위치
 * @param y		개체 세로 위치
 */
class Chest(world: World, x: Float, y: Float): Container(world, x, y, 24.0f, 25.0f, "chest.png");
