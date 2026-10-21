@file:JvmName("DesktopLauncher")
package io.potatogun.endlessdead.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

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
 */
fun main() {
	// 1) 게임 객체 만들기
	val game = EndlessDead();

	// 2) 창(Window) 설정
	val config = Lwjgl3ApplicationConfiguration().apply {
		setTitle("${Constants.GAME_TITLE} - Loading...");
		setWindowedMode(800, 600);
		setResizable(true);
		setWindowSizeLimits(640, 480, -1, -1);  // -1: 제한 없음
		useVsync(false);  // 수직동기화를 꺼야 랙이 줄어듦
		setForegroundFPS(Constants.PASSIVE_FPS);  // 타이틀 화면에서는 낮은 fps로
		setIdleFPS(Constants.PASSIVE_FPS);
		setWindowIcon(FileType.Internal, "assets/icon_16.bmp", "assets/icon_32.bmp", "assets/icon_128.bmp");
	};

	// 3) 실행
	Lwjgl3Application(game, config);
}
