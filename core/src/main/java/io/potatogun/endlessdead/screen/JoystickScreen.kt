package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

interface JoystickScreen {
	/**
	 * 조이스틱 객체를 가져온다. 일반적으로 모바일에서만 있다.
	 *
	 * 자바에서는 getJoystick()로 가져온다.
	 */
	val joystick: Touchpad?;
}
