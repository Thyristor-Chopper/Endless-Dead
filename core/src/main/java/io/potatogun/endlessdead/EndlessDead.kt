package io.potatogun.endlessdead;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Title;
import io.potatogun.endlessdead.screen.ZombieWorldViewer;
import io.potatogun.gdxhelper.Game;
import io.potatogun.gdxhelper.Window;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  이 프로젝트의 '게임 본체' — LibGDX의 Game을 상속해서 만든다.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  Game은 LibGDX가 제공하는 '게임 앱의 최상위 껍데기' 클래스다.
 *    - create()   : 앱 시작 시 한 번 호출 (초기화 자리)
 *    - render()   : 매 프레임 호출 (내부에서 현재 Screen.render를 대신 돌려줌)
 *    - setScreen(): 현재 Screen을 바꾸는 메서드
 *
 *  이 중 create()는 추상 메서드(ApplicationListener 인터페이스 상속).
 *  즉 Game을 상속하는 순간 반드시 create()를 구현해야 한다.
 *
 *  (안드로이드의 Activity.onCreate()와 완전히 같은 패턴이다.
 *   onCreate() 안에서 setContentView(...)로 첫 화면을 붙이듯,
 *   여기서는 create() 안에서 setScreen(...)으로 첫 Screen을 붙인다.)
 *
 *  왜 이 파일이 core에 있나?
 *    게임에는 특정 OS에 의존하는 코드가 전혀 없다 (LibGDX Game 상속뿐).
 *    따라서 데스크톱·안드로이드·iOS 어느 플랫폼에서 띄우든 그대로 쓸 수 있다.
 *    플랫폼별 런처(DesktopLauncher 등)만 따로 두면 된다.
 *
 *  자기 게임을 만들 때 고칠 곳:
 *   ▸ create() 안에서 setScreen에 넘기는 Screen을 자기 Screen으로 교체
 */
class EndlessDead : Game() {
	// 미리 등록된 스크린. val은 lateinit이 불가하여 lazy 위임 사용.
	internal val titleScreen: Title by lazy { Title(this) };
	internal val worldViewer: ZombieWorldViewer by lazy { ZombieWorldViewer(this) };

	/**
	 * LibGDX가 게임 시작 시 한 번 호출하는 라이프사이클 메서드.
	 *
	 * 이 함수는 'Gdx.graphics / Gdx.gl / Gdx.files 같은 전역이 모두 준비된 뒤'
	 * 호출되므로, Screen 안에서 SpriteBatch / BitmapFont / Texture 같은 LibGDX 자원을
	 * 만들어도 안전하다. (생성자에서 미리 Screen을 만들면 크래시 난다.)
	 *
	 * 보통 여기서 할 일:
	 *   1. 첫 화면이나 월드(Screen 또는 World의 자식)를 만들고
	 *   2. setScreen(...)으로 등록 → 이후 LibGDX 가 매 프레임 그 월드를 렌더
	 *
	 *  World가 LibGDX의 Screen 인터페이스를 상속하므로 setScreen 인자로 넘길 수 있다.
	 */
	override fun create() {
		setScreen(titleScreen);  // 부모 Game이 제공하는 메서드
		Window.setBaseTitle(Constants.GAME_TITLE);
	}

	/**
	 * 자원을 정리한다.
	 */
	override fun dispose() {
		super.dispose();
		Textures.disposeShared();  // 공유 자원 정리
		Item.textures.disposeShared();
		titleScreen.dispose();  // 이게 로딩이 안 됐을 리가 없다.
		worldViewer.dispose();
	}
}
