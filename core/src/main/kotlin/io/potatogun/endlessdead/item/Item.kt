package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.endlessdead.world.World;

/**
 * 아이템 추상 클래스
 *
 * @param world	아이템이 있는 세계
 * @param id	총 식별자
 * @param name	총 이름
 */
abstract class Item(val world: World, val id: String, val name: String) {
	/**
	 * 아이템을 들고 있는 개체
	 */
	val holder: InventoryEntity?
		get() {
			for(entity in world.getEntities()) {
				if(entity is InventoryEntity && entity.hasItem(this))
					return entity;
			}
			return null;
		};
	/**
	 * 아이템이 들어 있는 상자
	 * holder와 container 중 반드시 하나는 null이어야 한다.
	 */
	val container: Container?
		get() {
			for(entity in world.getEntities()) {
				if(entity is Container && entity.containedItem === this)
					return entity;
			}
			return null;
		};

	/**
	 * 같은 종류의 아이템인지를 비교한다.
	 */
	open fun equals(other: Item): Boolean {
		return id == other.id;
	}

	/**
	 * 아이템을 파괴한다.
	 *
	 * @return 아이템 존재 여부
	 */
	fun destroy(): Boolean {
		val first = holder?.let { it.removeItemFromInventory(this); true } ?: false;
		val second = container?.let { it.removeItem(); true } ?: false;
		
		cleanUp();
		
		return first || second;
		
		// 나머지는 jvm이나 달빅이 알아서 gc 해주겠지.
	}

	/**
	 * 아이템의 문자열 표현
	 */
	override fun toString(): String = name;

	/**
	 * 자원 정리
	 */
	internal open fun cleanUp() {}
}
