package io.potatogun.gdxhelper.pools;

import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.entity.Entity;

class EntityArrayPool @JvmOverloads constructor(private val capacity: Int, private val autoClear: Boolean = true) : Pool<GdxArray<Entity>>() {
	override fun newObject(): GdxArray<Entity> = GdxArray(false, capacity);

	override fun reset(obj: GdxArray<Entity>) {
		if(autoClear) obj.clear();
	}
}
