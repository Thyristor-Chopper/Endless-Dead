package io.potatogun.endlessdead.position;

import io.potatogun.endlessdead.entity.Entity;

import kotlin.math.sqrt;

// 원래는 data class Position(x, y)가 있고 MutablePosition이 Position을 상속하게 하려 했으나 
//   레코드는 상속이 불가능해서 복잡하지만 이렇게 했다.

/**
 * 위치(평면좌표)에 대한 인터페이스이다.
 */
interface Position {
	/**
	 * X 좌표
	 */
	val x: Float;
	/**
	 * Y 좌표
	 */
	val y: Float;

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
inline fun Position.toMutablePosition(): MutablePosition = MutablePosition(x, y);

/**
 * 위치(평면좌표)를 저장하는 레코드이다.
 *
 * @param x	X좌표
 * @param y	Y좌표
 */
private data class ImmutablePosition(override val x: Float, override val y: Float) : Position;

/**
 * 읽기 전용 Position 객체를 생성한다.
 * ImmutablePosition의 별칭처럼 작용한다. 위에서 설명했듯이 Position 자체가 원래는 레코드여야 했으나
 * 프로그래밍 언어 자체의 제약으로 이렇게 만들었다.
 */
fun Position(x: Float, y: Float): Position = ImmutablePosition(x, y);
