package io.potatogun.endlessdead.item;

import io.potatogun.endlessdead.inventory.Inventory;

/**
 * 아이템 추상 클래스
 *
 * 자바로 만들어진 게임들도 아이템에 대해 필드를 직접 노출하지 않고 getName() 등을 쓰는 경우가 많아서 @JvmField는 안 붙임
 *   음... Bukkit.broadcastMessage("This player has " + item.getName() + "!");
 *         Bukkit.broadcastMessage("This player has " + item.name + "!");
 *   자바에서 두 형식 다 쓰일 법할 것 같기도 하고...
 *
 * @property id   아이템 식별자
 * @property name 아이템 이름
 */
abstract class Item(@get:JvmName("getID") val id: String, val name: String) {
	/**
	 * 아이템을 들고 있는 인벤토리 (캐시)
	 */
	internal var inventory: Inventory? = null;

	/**
	 * 아이템을 파괴한다.
	 *
	 * @return 성공 여부
	 */
	fun destroy(): Boolean {
		return inventory?.removeItem(this) ?: true;  // 소유자가 없는 아이템은 그냥 없어지는 것이기 때문에 true로

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
