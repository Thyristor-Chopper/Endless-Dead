package com.oop.game.entity.container;

import com.oop.game.world.World;

// 상자 클래스
class Chest(world: World, x: Float, y: Float): Container(world, x, y, 32.0f, 32.0f, "chest.png");
