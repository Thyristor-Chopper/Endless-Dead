package io.potatogun.gdxhelper.pools;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.entity.Entity;

class EntitySetPool @JvmOverloads constructor(private val capacity: Int, private val autoClear: Boolean = true) : Pool<ObjectSet<Entity>>() {
	override fun newObject(): ObjectSet<Entity> = ObjectSet<Entity>(capacity);

	override fun reset(obj: ObjectSet<Entity>) {
		if(autoClear) obj.clear();
	}
}
