@file:JvmName("PositionExtensions")
package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * 이 위치와 지정한 개체 사이의 거리를 구한다.
 *
 * @param other 비교할 개체
 * @return      거리
 */
inline fun Position.distanceTo(other: Entity): Float = distanceTo(other.position);

/**
 * 수정 가능한 복사본 레코드를 반환한다.
 *
 * @return 수정 가능한 위치
 */
fun Position.toMutablePosition(): MutablePosition = MutablePosition(x, y);
