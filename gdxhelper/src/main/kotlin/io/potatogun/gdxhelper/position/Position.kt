package io.potatogun.gdxhelper.position;

import io.potatogun.gdxhelper.entity.Entity;

import java.util.Objects;

import kotlin.math.sqrt;

// 원래는 data class Position(x, y)가 있고 MutablePosition이 Position을 상속하게 하려 했으나 
//   레코드는 상속이 불가능해서 복잡하지만 이렇게 했다.

/**
 * 위치(평면좌표)에 대한 인터페이스이다.
 */
open class Position(open val x: Float, open val y: Float) {
	/**
	 * 두 위치 사이의 거리를 구한다.
	 *
	 * @return 거리
	 */
	fun distanceTo(other: Position): Float {
		val dx = x - other.x;
		val dy = y - other.y;
		return sqrt(dx * dx + dy * dy);
	}

	override fun equals(other: Any?) = other is Position && other.x == x && other.y == y;

	override fun hashCode(): Int {
		return Objects.hash(x, y);
	}
}

// 확장 함수

/**
 * 이 위치와 지정한 개체 사이의 거리를 구한다.
 *
 * @return 거리
 */
inline fun Position.distanceTo(other: Entity): Float = distanceTo(other.position);

/**
 * 수정 가능한 복사본 레코드를 반환한다.
 */
inline fun Position.toMutablePosition(noinline onChange: (Float, Float) -> Unit = { _, _ -> }): MutablePosition = MutablePosition(x, y, onChange);
