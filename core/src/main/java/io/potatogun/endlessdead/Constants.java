package io.potatogun.endlessdead;

import org.jetbrains.annotations.NotNull;

/**
 * 게임에서 필요한 상수들이다.
 */
public final class Constants {
	private Constants() {
		throw new UnsupportedOperationException("this class cannot be instantiated");
	}

	/**
	 * 목표로 표시할 최대 FPS
	 */
	public static final int FPS = 60;
	/**
	 * 중지 상태일 때 FPS
	 */
	public static final int PASSIVE_FPS = 20;
	/**
	 * 월드의 너비
	 */
	public static final float ZOMBIE_WORLD_WIDTH = 2000f;
	/**
	 * 월드의 높이
	 */
	public static final float ZOMBIE_WORLD_HEIGHT = 2000f;
	/**
	 * 게임 제목
	 */
	@NotNull public static final String GAME_TITLE = "Endless Dead";
}
