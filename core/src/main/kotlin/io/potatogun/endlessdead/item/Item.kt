package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.World;

/**
 * 아이템 추상 클래스
 *
 * @param world	아이템이 있는 세계
 * @param id	아이템 식별자 - camelCase에서 두문자어는 모두 대문자라고 해서 getID 게터로 했다. 당장 DesktopLauncher의 config에서도 setForegroundFps가 아닌 setForegroundFPS이다.
 * @param name	아이템 이름
 *
 * 자바로 만들어진 게임들도 아이템에 대해 필드를 직접 노출하지 않고 getName() 등을 쓰는 경우가 많아서 @JvmField는 안 붙임
 *   음... Bukkit.broadcastMessage("This player has " + item.getName() + "!");
 *         Bukkit.broadcastMessage("This player has " + item.name + "!");
 *   자바에서 두 형식 다 쓰일 법할 것 같기도 하고...
 */
abstract class Item(val world: World, @get:JvmName("getID") val id: String, val name: String) {
	/**
	 * 아이템을 들고 있는 개체
	 */
	val holder: Entity?
		get() {
			for(entity in world.getEntities()) {
				if(entity is InventoryEntity && entity.hasItem(this))
					return entity;
			}
			return null;
		};
	/**
	 * 아이템이 들어 있는 상자
	 * 정상적인 상황이라면 holder와 container 중 반드시 하나는 null이어야 한다.
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
	 * 아이템을 파괴한다.
	 *
	 * @return 아이템 존재 여부
	 */
	fun destroy(): Boolean {
		val first = holder?.let {
			if(it is InventoryEntity) {
				it.removeItemFromInventory(this);
				it.onItemDestoryed(this);

				true
			} else {
				false
			}
		} ?: false;
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
