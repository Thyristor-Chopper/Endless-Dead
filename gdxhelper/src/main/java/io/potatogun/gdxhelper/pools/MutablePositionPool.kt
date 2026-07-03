package io.potatogun.gdxhelper.pools;

import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.position.MutablePosition;

class MutablePositionPool() : Pool<MutablePosition>() {
	override fun newObject(): MutablePosition = MutablePosition(0f, 0f);

	inline fun obtain(xPos: Float, yPos: Float): MutablePosition = obtain().apply {
		x = xPos;
		y = yPos;
	};
}
