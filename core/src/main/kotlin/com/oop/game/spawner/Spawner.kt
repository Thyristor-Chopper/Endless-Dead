package com.oop.game.spawner;

import com.oop.game.entity.Entity;
import com.oop.game.world.World;

/**
 * 개체 소환기 인터페이스
 */
interface Spawner {
	val world: World;

	fun tick(delta: Float);
}
