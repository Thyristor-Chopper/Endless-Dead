@file:JvmName("DesktopLauncher")
package io.potatogun.endlessdead.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

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

	// 창 이벤트 감시자 생성
	val windowListener = object : Lwjgl3WindowListener {
		private var closeDialogShowing = false;

		override fun closeRequested(): Boolean {
			if(!game.gameManager.isPlaying && !game.gameManager.isPaused)
				return true;

			closeDialogShowing = true;
			val result = TinyFileDialogs.tinyfd_messageBox(Constants.GAME_TITLE, "Are you sure you want to quit ${Constants.GAME_TITLE}?  ", "yesno", "question", true);
			return result;
		}

		override fun created(window: Lwjgl3Window) {}

		override fun filesDropped(files: Array<String>) {}

		override fun focusGained() {}

		override fun focusLost() {
			if(game.gameManager.isPlaying && !closeDialogShowing)
				game.gameManager.pause();
			if(closeDialogShowing)
				closeDialogShowing = false;
		}

		override fun iconified(isIconified: Boolean) {}

		override fun maximized(isMaximized: Boolean) {}

		override fun refreshRequested() {}
	};

	// 창(Window) 설정
	val config = Lwjgl3ApplicationConfiguration().apply {
		setTitle("${Constants.GAME_TITLE} - Loading...");
		setWindowedMode(800, 600);
		setResizable(true);
		setWindowSizeLimits(640, 480, -1, -1);  // -1: 제한 없음
		useVsync(false);  // 수직동기화를 꺼야 랙이 줄어듦
		setForegroundFPS(Constants.PASSIVE_FPS);  // 첫화면은 타이틀 화면이므로 낮은 fps로
		setIdleFPS(1);  // 최소화됐을 땐 1 FPS
		setWindowIcon(FileType.Internal, "assets/icon_16.bmp", "assets/icon_32.bmp", "assets/icon_48.bmp");
		setWindowListener(windowListener);
	};

	// 실행
	Lwjgl3Application(game, config);
}
