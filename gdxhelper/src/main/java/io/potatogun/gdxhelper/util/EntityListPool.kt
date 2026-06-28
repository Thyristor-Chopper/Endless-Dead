package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.entity.Entity;

class EntityListPool : Pool<MutableList<Entity>>() {
	override fun newObject(): MutableList<Entity> = mutableListOf<Entity>();

	override fun reset(obj: MutableList<Entity>) {
		obj.clear();
	}
}
