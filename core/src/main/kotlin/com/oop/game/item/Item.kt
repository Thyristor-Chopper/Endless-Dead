package com.oop.game.item;

import com.oop.game.GameObject;
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
abstract class Item(override val world: World, val id: String, val name: String) : GameObject, WorldObject, Updatable {
	override val game = world.game;
	var holder: InventoryEntity? = null
		internal set;
	
	/**
	 * 같은 종류의 아이템인지를 비교한다.
	 */
	open fun equals(other: Item): Boolean {
		return id == other.id;
	}
	
	/**
	 * 인벤토리를 가진 개체가 이 아이템을 들고 있는 경우 파괴한다.
	 *
	 * @return 성공 여부
	 */
	inline fun destroy(): Boolean {
		return holder?.removeItemFromInventory(this) ?: false;
		
		// 나머지는 jvm이나 달빅이 알아서 gc 해주겠지.
	}
	
	/**
	 * 아이템의 문자열 표현
	 */
	override fun toString(): String = name;
}
