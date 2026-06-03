package io.potatogun.endlessdead.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Files.FileType;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;

import kotlin.concurrent.thread;

/**
 * ════════════════════════════════════════════════════════════
 *  데스크톱에서 게임을 실행시키는 진입점 (main 함수).
 * ════════════════════════════════════════════════════════════
 *
 *  여기서 하는 일은 세 가지뿐:
 *   1. 게임 객체(EndlessDead)를 만든다
 *   2. 창(Window) 설정을 만든다
 *   3. LibGDX에게 "이 게임을 이 설정으로 실행시켜줘"라고 넘긴다
 *
 *  실제 게임 내용은 EndlessDead 클래스와 각 Screen/World 클래스에서 정의된다.
 *  이 파일은 순수 'OS에 창 띄우기' 역할만 한다.
 */
fun main() {
    // ─────────────────────────────────────────
    // 1) 게임 객체 만들기
    // ─────────────────────────────────────────
    //   EndlessDead은 LibGDX의 Game을 상속한 클래스 (EndlessDead.kt 참고).
    //   이 시점에는 단순히 설계도만 들고 있을 뿐, 실제 화면은 아직 안 만들어진다.
    //   화면 생성은 LibGDX가 나중에 game.create()를 호출할 때 일어난다.
    val game = EndlessDead();

    // ─────────────────────────────────────────
    // 2) 창(Window) 설정
    // ─────────────────────────────────────────
    //   창 제목, 크기, FPS 등을 Lwjgl3ApplicationConfiguration 객체에 담는다.
    //   ('Lwjgl3' = Lightweight Java Game Library 3 — LibGDX의 데스크톱 백엔드)
    //
    // TODO (10주차 이후): 영역함수 'apply'를 배우면
    //   Lwjgl3ApplicationConfiguration().apply { setTitle(...); setWindowedMode(...); ... }
    //   처럼 더 간결하게 쓸 수 있다.
    val config = Lwjgl3ApplicationConfiguration().apply {
		setTitle("${Constants.GAME_TITLE} - Loading...");		// 창 제목
		setWindowedMode(800, 600);								// 창 크기
		setResizable(true);									// 크기 조절 가능
		setWindowSizeLimits(320, 240, Constants.ZOMBIE_WORLD_WIDTH.toInt(), Constants.ZOMBIE_WORLD_HEIGHT.toInt());
		useVsync(false);										// 수직동기화를 꺼야 랙이 줄어듦
		setForegroundFPS(Constants.PASSIVE_FPS);				// 타이틀 화면에서는 낮은 fps로
		setIdleFPS(Constants.PASSIVE_FPS);	
		setWindowIcon(FileType.Internal, "assets/icon_16.png", "assets/icon_32.png", "assets/icon_128.png");
	};

    // ─────────────────────────────────────────
    // 3) 실행
    // ─────────────────────────────────────────
    //   Lwjgl3Application 생성자 호출 자체가 전체 게임을 시작시킨다.
    //   내부에서 차례로:
    //     ① OS 창을 띄우고
    //     ② OpenGL 컨텍스트와 Gdx.* 전역을 세팅한 뒤
    //     ③ game.create()를 호출 → EndlessDead#create() 안의 setScreen(...)이 실행되어
    //        타이틀 화면이 만들어진다
    //     ④ 매 프레임 Screen#render(delta)를 호출하며 루프를 돈다
    //   창이 닫힐 때까지 이 호출은 반환하지 않는다.
    Lwjgl3Application(game, config);
}
