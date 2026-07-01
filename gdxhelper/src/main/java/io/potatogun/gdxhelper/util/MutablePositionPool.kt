package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Pool;

class MutablePositionPool() : Pool<MutablePosition>() {
	override fun newObject(): MutablePosition = MutablePosition(0f, 0f);

	inline fun obtain(xPos: Float, yPos: Float): MutablePosition = obtain().apply {
		x = xPos;
		y = yPos;
	};
}
