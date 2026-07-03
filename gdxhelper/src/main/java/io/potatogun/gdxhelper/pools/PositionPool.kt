package io.potatogun.gdxhelper.pools;

import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.position.MutablePosition;
import io.potatogun.gdxhelper.position.Position;

// 의도적으로 내부적으로는 MutablePosition 사용. 일반 Position으로 풀을 만드는 건 말이 안 된다.
class PositionPool() : Pool<Position>() {
	override fun newObject(): Position = MutablePosition(0f, 0f) as Position;

	inline fun obtain(xPos: Float, yPos: Float): Position = (obtain() as MutablePosition).apply {
		x = xPos;
		y = yPos;
	} as Position;
}
