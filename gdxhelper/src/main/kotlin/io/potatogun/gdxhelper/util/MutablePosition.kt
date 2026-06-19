package io.potatogun.gdxhelper.util;

import kotlin.properties.Delegates;

/**
 * 위치(평면좌표)를 저장하는 수정 가능한 레코드이다.
 *
 * @param x	X좌표
 * @param y	Y좌표
 */
class MutablePosition(initialX: Float, initialY: Float, private val onChange: ((x: Float, y: Float) -> Unit)? = null) : Position(initialX, initialY) {
	override var x: Float by Delegates.observable(initialX) { _, _, _ -> onChange?.invoke(this.x, this.y) };
	override var y: Float by Delegates.observable(initialY) { _, _, _ -> onChange?.invoke(this.x, this.y) };
}
