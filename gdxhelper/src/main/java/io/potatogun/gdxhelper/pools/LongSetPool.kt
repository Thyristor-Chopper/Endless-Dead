package io.potatogun.gdxhelper.pools;

import com.badlogic.gdx.utils.LongSet;
import com.badlogic.gdx.utils.Pool;

class LongSetPool(private val capacity: Int) : Pool<LongSet>() {
	override fun newObject(): LongSet = LongSet(capacity);
}
