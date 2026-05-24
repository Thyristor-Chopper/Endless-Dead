package com.oop.game;

import com.oop.game.world.World;

/**
 * 특정 월드에 속해 있다는 개념이 있는 오브젝트
 */
interface WorldObject {
	val world: World;
}
