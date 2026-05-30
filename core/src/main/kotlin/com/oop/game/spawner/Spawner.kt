package com.oop.game.spawner;

import com.oop.game.entity.Entity;
import com.oop.game.world.World;

/**
 * 개체 소환기 인터페이스
 */
interface Spawner {
	/**
	 * 매 프레임 실행하는 서브루틴
	 */
	fun tick(delta: Float);
	
	fun cleanUp() {}
}
