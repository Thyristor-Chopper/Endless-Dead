package com.oop.game.item;

import com.oop.game.world.World;

/**
 * 아이템 추상 클래스
 *
 * @param world	아이템이 있는 세계
 * @param id	총 식별자
 * @param name	총 이름
 */
abstract class Item(val world: World, val id: String, val name: String) {
	// TODO 월드에서 update 호출
	open fun update(delta: Float) {}

	fun equals(other: Item): Boolean {
		return id == other.id;
	}
}
