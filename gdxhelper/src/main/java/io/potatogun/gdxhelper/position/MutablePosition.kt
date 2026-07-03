package io.potatogun.gdxhelper.position;

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
open class MutablePosition(override var x: Float, override var y: Float) : Position(x, y) {
	/**
	 * X 좌표에 지정한 값만큼 더한다 (자바 전용, 코틀린은 x += ... 사용).
	 *
	 * @param toAdd 더할 양
	 */
	fun addX(toAdd: Float) {
		x += toAdd;
	}

	/**
	 * Y 좌표에 지정한 값만큼 더한다 (자바 전용, 코틀린은 y += ... 사용).
	 *
	 * @param toAdd 더할 양
	 */
	fun addY(toAdd: Float) {
		y += toAdd;
	}

	// MutablePosition은 의도적으로 메모리 주소 기반으로 동작
	//   x, y가 같아도 메모리 상에서 서로 다른 객체면 해시맵에서 다른 것으로 취급한다.
	override fun hashCode(): Int = System.identityHashCode(this);

	override fun copy(x: Float, y: Float): MutablePosition = MutablePosition(x, y);
}
