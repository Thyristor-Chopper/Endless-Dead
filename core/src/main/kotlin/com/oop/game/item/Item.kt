package com.oop.game.item;

import com.oop.game.ItemHolder;
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
	internal var holder: ItemHolder? = null;
	
	fun equals(other: Item): Boolean {
		return id == other.id;
	}
	
	fun destroy() {
		val holder: ItemHolder? = this.holder;
		when(holder) {  // null이면 아무 작업도 안 됨
			is InventoryEntity	-> holder.removeItemFromInventory(this);
			is Container		-> holder.removeItem();
		}
		
		// 나머지는 jvm이나 달빅이 알아서 gc 해주겠지.
	}
}
