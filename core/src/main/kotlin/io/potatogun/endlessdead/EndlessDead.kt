package io.potatogun.endlessdead;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.potatogun.endlessdead.Window;
import io.potatogun.endlessdead.screen.Title;
import io.potatogun.endlessdead.screen.WorldViewer;

// 일부 로우 레벨 최적화 관련 참고 사항
// - public/protected이고 null이 아니거나 val인 것들은 약간의 오버헤드 감소를 위해 @JvmField를 붙였다. 확실히 코틀린만의 보호장치를 우회할 위험이 없다고 생각되는 곳에만 붙였으며 int, float는 직접 빌드 후 디컴파일러로 Java로 디컴파일하여 랩퍼 Integer나 Float가 아닌 null 불가 원시 int, float임을 확인했다.
// - 아무 함수나 마구잡이로 인라인화하지 않았고 한 줄짜리 간단한 함수나 딱 한 곳에서만 호출되는(update 내에서 세부 로직을 구현한 메쏘드들을 호출한 것 등) 함수들만 명시적 인라인화를 했다. 

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  이 프로젝트의 '게임 본체' — LibGDX의 Game을 상속해서 만든다.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  Game은 LibGDX 가 제공하는 '게임 앱의 최상위 껍데기' 클래스다.
 *    - create()   : 앱 시작 시 한 번 호출 (초기화 자리)
 *    - render()   : 매 프레임 호출 (내부에서 현재 Screen.render 를 대신 돌려줌)
 *    - setScreen(): 현재 Screen 을 바꾸는 메서드
 *
 *  이 중 create()는 추상 메서드(ApplicationListener 인터페이스 상속).
 *  즉 Game을 상속하는 순간 반드시 create() 를 구현해야 한다.
 *
 *  (안드로이드의 Activity.onCreate()와 완전히 같은 패턴이다.
 *   onCreate() 안에서 setContentView(...)로 첫 화면을 붙이듯,
 *   여기서는 create() 안에서 setScreen(...)으로 첫 Screen 을 붙인다.)
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
	// 미리 등록된 스크린
	val titleScreen: Title by lazy { Title(this) };
	// 매니저 (게임 클래스 인스턴스당 하나)
	// @JvmField이지만 val이라 바꾸지도 못하고 클래스 생성 시 자동으로 만들어지므로 외부 자바 프로그램에 의해 null로 바뀔 여지가 없다.
	@JvmField val gameManager = GameManager(this);
	@JvmField val scoreManager = ScoreManager(this);

    /**
     * LibGDX 가 게임 시작 시 한 번 호출하는 라이프사이클 메서드.
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
		Window.setTitle(Constants.GAME_TITLE);
    }

	/**
	 * Gdx.graphics.width를 매번 실수형으로 변환하는 오버헤드를 없애기 위해 창 크기를 캐시하고 크기가 바뀔 때만 업데이트한다.
	 *
	 * 우리 학교 개발자 중에 나 만큼 최적화에 미친 사람이 또 있을까...
	 */
	override fun resize(width: Int, height: Int) {
		Window.updateWindowDimensions();
		super.resize(width, height);
	}

	/**
	 * 공유 자원을 정리한다.
	 */
	override fun dispose() {
		Textures.disposeShared();
	}
}
