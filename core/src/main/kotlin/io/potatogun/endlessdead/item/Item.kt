package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

/**
 * 아이템 추상 클래스
 *
 * 자바로 만들어진 게임들도 아이템에 대해 필드를 직접 노출하지 않고 getName() 등을 쓰는 경우가 많아서 @JvmField는 안 붙임
 *   음... Bukkit.broadcastMessage("This player has " + item.getName() + "!");
 *         Bukkit.broadcastMessage("This player has " + item.name + "!");
 *   자바에서 두 형식 다 쓰일 법할 것 같기도 하고...
 *
 * @property world 아이템이 있는 세계
 * @property id    아이템 식별자
 * @property name  아이템 이름
 */
abstract class Item(val world: World, @get:JvmName("getID") val id: String, val name: String) {
	/**
	 * 아이템을 들고 있는 개체
	 */
	val holder: Entity?
		get() {
			for(entity in world.getEntities()) {
				if(entity is InventoryEntity && entity.inventory.hasItem(this))
					return entity;
			}
			return null;
		};

	/**
	 * 아이템을 파괴한다.
	 *
	 * @return 성공 여부
	 */
	fun destroy(): Boolean {
		val removed = holder?.let {
			if(it is InventoryEntity) {
				it.inventory.removeItem(this)
			} else {
				false
			}
		} ?: false;

		if(removed) cleanUp();

		return removed;

		// 나머지는 jvm이나 달빅이 알아서 gc 해주겠지.
	}

	/**
	 * 아이템의 문자열 표현
	 *
	 * @return 문자열 표현
	 */
	override fun toString(): String = name;

	/**
	 * 자원을 정리한다.
	 */
	open fun cleanUp() {}
}
