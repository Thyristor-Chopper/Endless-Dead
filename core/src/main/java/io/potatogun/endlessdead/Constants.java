package io.potatogun.endlessdead;

import org.jetbrains.annotations.NotNull;

/**
 * 게임에서 필요한 상수들이다.
 *
 * 불필요한 INSTANCE 필드와 인스턴스를 만드는 오버헤드를 없애기 위해서 그냥 자바로 바꾸었다.
 *   이렇게 해도 여전히 상수는 인라인으로 바로 대입된다. (디컴파일해서 확인)
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
	/**
	 * 아이템 텍스처의 한 변 크기
	 */
	public static final float ITEM_SIZE = 24f;
}
