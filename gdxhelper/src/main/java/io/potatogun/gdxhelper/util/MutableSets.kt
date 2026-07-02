@file:JvmName("MutableSets")
package io.potatogun.gdxhelper.util;

/**
 * 지정한 요소들을 담아서 약참조 집합을 만든다.
 *
 * @param items 담을 요소들
 * @return      만들어진 집합
 */
inline fun <T> weakMutableSetOf(vararg items: T): WeakMutableSet<T> = WeakMutableSet<T>().apply { addAll(items) };

/**
 * 지정한 요소들을 담아서 메모리 주소 참조 집합을 만든다.
 *
 * @param items 담을 요소들
 * @return      만들어진 집합
 */
inline fun <T> identityMutableSetOf(vararg items: T): IdentityMutableSet<T> = IdentityMutableSet<T>().apply { addAll(items) };
