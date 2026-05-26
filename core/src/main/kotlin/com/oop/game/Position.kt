package com.oop.game;

import kotlin.math.sqrt;

/**
 * 위치(평면좌표)를 저장하는 immutable 레코드
 *
 * @param x	X좌표
 * @param y	Y좌표
 */
data class Position(val x: Float, val y: Float) {
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
