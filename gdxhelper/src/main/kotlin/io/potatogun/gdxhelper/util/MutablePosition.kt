package io.potatogun.gdxhelper.util;

import kotlin.properties.Delegates;

/**
 * 위치(평면좌표)를 저장하는 수정 가능한 레코드이다.
 *
 * 해시 코드 동작이 다르기 때문에 한 해시맵/세트에서 Position과 MutablePosition을
 *   섞어 쓰지 말 것.
 *
 * ---- 예제 ----
 *   val x = MutablePosition(1f, 2f);
 *   val y = MutablePosition(1f, 2f);
 *   val mp = mutableMapOf<MutablePosition, Int>();
 *   mp[x] = 1;
 *   println(mp[x]);  // 1
 *   println(mp[y]);  // null - 둘이 메모리 상에서 다른 개체이기 때문
 *   println(x == y);  // true - equals를 override했기 때문
 *   val a = Position(1f, 2f);
 *   val b = Position(1f, 2f);
 *   val mp2 = mutableMapOf<Position, Int>();
 *   mp2[a] = 2;
 *   println(mp2[a]);  // 2
 *   println(mp2[b]);  // 2 - 메모리 상에서 다른 개체이지만 동일한 값에 대해 같은 해시를 생성하기 때문
 *   println(a == b);  // true - equals를 override했기 때문
 *
 * @param x			X좌표
 * @param y			Y좌표
 * @param onChange	값이 바뀔 때 콜백 함수
 */
class MutablePosition(initialX: Float, initialY: Float, private val onChange: ((x: Float, y: Float) -> Unit)? = null) : Position(initialX, initialY) {
	override var x: Float by Delegates.observable(initialX) { _, _, _ -> onChange?.invoke(this.x, this.y) };
	override var y: Float by Delegates.observable(initialY) { _, _, _ -> onChange?.invoke(this.x, this.y) };

	// MutablePosition은 의도적으로 메모리 주소 기반으로 동작
	//   x, y가 같아도 서로 다른 객체면 해시맵에서 다른 것으로 취급한다.
	override fun hashCode(): Int = System.identityHashCode(this);

	fun copy(x: Float = this.x, y: Float = this.y, onChange: ((x: Float, y: Float) -> Unit)? = this.onChange): MutablePosition = MutablePosition(x, y, onChange);

	fun component3(): ((x: Float, y: Float) -> Unit)? = onChange;
}
