package com.oop.game.entity.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.oop.game.item.Item;
import com.oop.game.world.World;

/**
 * 건물
 *
 * @param world	개체가 속한 세계
 * @param x		개체 가로 위치
 * @param y		개체 세로 위치
 * @param initialItem	처음 들어있는 아이템
 */
class Building(world: World, x: Float, y: Float, initialItem: Item? = null): Container(world, x, y, 23.0f, 24.0f, "building.png", initialItem) {
	override protected val emptyTexture = Texture(Gdx.files.internal("empty_building.png"));
}
