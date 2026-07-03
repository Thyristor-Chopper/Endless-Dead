package io.potatogun.gdxhelper.pools;

import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.entity.Entity;

class EntityListPool(private val autoClear: Boolean = true) : Pool<MutableList<Entity>>() {
	override fun newObject(): MutableList<Entity> = mutableListOf<Entity>();

	override fun reset(obj: MutableList<Entity>) {
		if(autoClear) obj.clear();
	}
}
