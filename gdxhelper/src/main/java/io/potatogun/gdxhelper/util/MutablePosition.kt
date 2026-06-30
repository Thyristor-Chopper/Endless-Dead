package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.ObjectMap;

import kotlin.properties.Delegates;

import java.util.function.BiConsumer;

/**
 * 위치(평면좌표)를 저장하는 수정 가능한 레코드이다.
 *
 * 해시 처리 방식이 다르기 때문에 한 해시맵에서 Position과 MutablePosition을
 *   섞어 쓰지 말 것. 그리고 한 해시세트에는 Position은 같은 x, y당 하나만 있지만
 *   MutablePosition은 x, y가 같아도 여러 개가 들어간다. 의도된 동작이다.
 *   원래 계약상으로는 equals가 참이면 hashCode도 같아야 한다지만 맵 키로써의 안전성과 
 *   실제 사용 시의 체감과 목적에 기반하여 의도적으로 깬다고 나는 당당히 말한다.
 *
 * ---- 예제 ----
 *   val x = MutablePosition(1f, 2f);
 *   val y = MutablePosition(1f, 2f);
 *   val mp = mutableMapOf<MutablePosition, Int>();
 *   mp[x] = 1;
 *   println(mp[x]);  // 1
 *   println(mp[y]);  // null - 둘이 메모리 상에서 다른 객체이기 때문
 *   println(x == y);  // true - equals를 override했기 때문
 *   val a = Position(1f, 2f);
 *   val b = Position(1f, 2f);
 *   val mp2 = mutableMapOf<Position, Int>();
 *   mp2[a] = 2;
 *   println(mp2[a]);  // 2
 *   println(mp2[b]);  // 2 - 메모리 상에서 다른 객체이지만 동일한 값에 대해 같은 해시를 생성하기 때문
 *   println(a == b);  // true - equals를 override했기 때문
 *
 * @property x 처음 X 좌표
 * @property y 처음 Y 좌표
 */
class MutablePosition(x: Float, y: Float) : Position(x, y) {
	private val changeHandlers = GdxArray<(x: Float, y: Float) -> Unit>(false, 2);
	private val javaHandlerMap = ObjectMap<BiConsumer<Float, Float>, (x: Float, y: Float) -> Unit>(2);
	override var x: Float by Delegates.observable(x) { _, _, _ -> invokeObservers() };
	override var y: Float by Delegates.observable(y) { _, _, _ -> invokeObservers() };

	/**
	 * 값이 바뀔 때 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백
	 */
	fun addObserver(handler: BiConsumer<Float, Float>) {
		val ktHandler: (Float, Float) -> Unit = { x, y -> handler.accept(x, y) };
		javaHandlerMap.put(handler, ktHandler);
		changeHandlers.add(ktHandler);
	}

	/**
	 * 값이 바뀔 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	@JvmSynthetic fun addObserver(handler: (x: Float, y: Float) -> Unit) {
		changeHandlers.add(handler);
	}

	/**
	 * 값이 바뀔 때 콜백을 해제한다 (자바에서 사용).
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeObserver(handler: BiConsumer<Float, Float>) {
		javaHandlerMap[handler]?.let {
			changeHandlers.removeValue(it, true);
			javaHandlerMap.remove(handler);
		};
	}

	/**
	 * 값이 바뀔 때 콜백을 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	@JvmSynthetic fun removeObserver(handler: (x: Float, y: Float) -> Unit) {
		changeHandlers.removeValue(handler, true);
	}

	private inline fun invokeObservers() {
		for(i in 0 until changeHandlers.size)
			changeHandlers[i](x, y);
	}

	// MutablePosition은 의도적으로 메모리 주소 기반으로 동작
	//   x, y가 같아도 메모리 상에서 서로 다른 객체면 해시맵에서 다른 것으로 취급한다.
	override fun hashCode(): Int = System.identityHashCode(this);

	override fun copy(x: Float, y: Float): MutablePosition = MutablePosition(x, y);
}
