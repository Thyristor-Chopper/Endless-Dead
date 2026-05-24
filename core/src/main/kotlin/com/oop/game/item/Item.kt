package com.oop.game.item;

import com.oop.game.Updatable;
import com.oop.game.WorldObject;
import com.oop.game.entity.InventoryEntity;
import com.oop.game.entity.container.Container;
import com.oop.game.world.World;

/**
 * 아이템 추상 클래스
 *
 * @param world	아이템이 있는 세계
 * @param id	총 식별자
 * @param name	총 이름
 */
abstract class Item(world: World, val id: String, val name: String) : WorldObject, Updatable {
	override val world = world;
	internal var toBeDestroyed = false;  // world에서 접근 필요
	internal var holder: InventoryEntity? = null;
	internal var container: Container? = null;
	
	fun equals(other: Item): Boolean {
		return id == other.id;
	}
	
	fun destroy() {
		toBeDestroyed = true;
		// 나머지는 world에서 처리
	}
}
