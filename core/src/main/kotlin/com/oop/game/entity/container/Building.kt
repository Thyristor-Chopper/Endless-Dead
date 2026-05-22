package com.oop.game.entity.container;

import com.oop.game.world.World;

// 건물 클래스
class Building(world: World, x: Float, y: Float): Container(world, x, y, 32.0f, 32.0f, "building.png");
