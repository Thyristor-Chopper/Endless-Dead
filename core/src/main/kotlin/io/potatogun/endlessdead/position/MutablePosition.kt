package io.potatogun.endlessdead.position;

/**
 * 위치(평면좌표)를 저장하는 수정 가능한 레코드이다.
 *
 * @param x	X좌표
 * @param y	Y좌표
 */
data class MutablePosition(override var x: Float, override var y: Float) : Position;
