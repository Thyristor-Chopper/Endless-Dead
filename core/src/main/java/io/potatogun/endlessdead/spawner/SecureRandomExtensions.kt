@file:JvmName("SecureRandomExtensions")
package io.potatogun.gdxhelper.util;

import java.security.SecureRandom;

/**
 * 0부터 지정한 수까지 무작위 4바이트 부동소수점 실수를 생성한다.
 *
 * @param max 끝 범위
 */
inline fun SecureRandom.nextFloat(max: Float): Float = nextFloat() * max;

/**
 * 지정한 범위에서 무작위 4바이트 부동소수점 실수를 생성한다.
 *
 * @param min 시작 범위
 * @param max 끝 범위
 */
inline fun SecureRandom.nextFloat(min: Float, max: Float): Float = min + (nextFloat() * (max - min));

/**
 * 무작위 부호(-1 또는 1)을 생성한다.
 */
inline fun SecureRandom.nextSign(): Int = if(nextBoolean()) 1 else -1;
