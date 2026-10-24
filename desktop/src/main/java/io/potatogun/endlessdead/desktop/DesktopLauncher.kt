@file:JvmName("DesktopLauncher")
package io.potatogun.endlessdead.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;

/**
 * 데스크톱에서 게임을 실행시키는 진입점(main 함수)이다.
 *
 * 여기서 하는 일은 세 가지뿐:
 *   1. 게임 객체(EndlessDead)를 만든다
 *   2. 창(Window) 설정을 만든다
 *   3. LibGDX에게 "이 게임을 이 설정으로 실행시켜줘"라고 넘긴다
 *
 * 실제 게임 내용은 EndlessDead 클래스와 각 Screen/World 클래스에서 정의된다.
 * 이 파일은 순수 'OS에 창 띄우기' 역할만 한다.
 *
 * @param args 명령줄 매개변수
 */
fun main(args: Array<String>) {
	// 게임 객체 만들기
	val game = EndlessDead();

	// 창(Window) 설정
	val config = LwjglApplicationConfiguration().apply {
		title = "${Constants.GAME_TITLE} - Loading...";
		width = 800;
		height = 600;
		resizable = true;
		vSyncEnabled = false;  // 수직동기화를 꺼야 랙이 줄어듦
		foregroundFPS = Constants.FPS;
		backgroundFPS = Constants.PASSIVE_FPS;
		pauseWhenBackground = true;
		pauseWhenMinimized = true;
		addIcon("assets/icon_128.bmp", FileType.Internal);
		addIcon("assets/icon_32.bmp", FileType.Internal);
		addIcon("assets/icon_16.bmp", FileType.Internal);
	};

	// 실행
	LwjglApplication(game, config);
}
