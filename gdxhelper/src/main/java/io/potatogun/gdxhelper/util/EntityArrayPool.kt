package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.entity.Entity;

class EntityArrayPool(private val autoClear: Boolean = true) : Pool<GdxArray<Entity>>() {
	override fun newObject(): GdxArray<Entity> = GdxArray(false, 128);

	override fun reset(obj: GdxArray<Entity>) {
		if(autoClear) obj.clear();
	}
}
