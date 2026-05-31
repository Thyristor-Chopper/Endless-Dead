package io.potatogun.endlessdead;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.potatogun.endlessdead.screen.Title;

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
 *    게임에는 특정 OS 에 의존하는 코드가 전혀 없다 (LibGDX Game 상속뿐).
 *    따라서 데스크톱·안드로이드·iOS 어느 플랫폼에서 띄우든 그대로 쓸 수 있다.
 *    플랫폼별 런처(DesktopLauncher 등)만 따로 두면 된다.
 *
 *  자기 게임을 만들 때 고칠 곳:
 *   ▸ create() 안에서 setScreen에 넘기는 Screen을 자기 Screen으로 교체
 */
class EndlessDead() : Game() {
	// 게임 제목
	val title = "Endless Dead";
    /** 
	 * 화면(창) 크기
	 */
    val screenWidth: Float
		inline get() = Gdx.graphics.getWidth().toFloat();
    val screenHeight: Float
		inline get() = Gdx.graphics.getHeight().toFloat();
	private var titleBarInfo = "";
	private var titleBarStats = "";
	/**
	 * 현재 라운드 (0이면 아직 게임이 시작되지 않은 것)
	 */
	var currentRound = 0
		internal set;

    /**
     * LibGDX 가 게임 시작 시 한 번 호출하는 라이프사이클 메서드.
     *
     * 이 함수는 'Gdx.graphics / Gdx.gl / Gdx.files 같은 전역이 모두 준비된 뒤'
     * 호출되므로, Screen 안에서 SpriteBatch / BitmapFont / Texture 같은 LibGDX 자원을
     * 만들어도 안전하다. (생성자에서 미리 Screen 을 만들면 크래시 난다.)
     *
     * 보통 여기서 할 일:
     *   1. 첫 화면이나 월드(Screen 또는 World의 자식) 를 만들고
     *   2. setScreen(...)으로 등록 → 이후 LibGDX 가 매 프레임 그 월드를 렌더
     *
     *  World가 LibGDX의 Screen 인터페이스를 상속하므로 setScreen 인자로 넘길 수 있다.
     */
    override fun create() {
        setScreen(Title(this));  // 부모 Game이 제공하는 메서드
    }

	/**
	 * 매 프레임 실행된다.
	 * update를 호출하여 매 프레임 게임 자체의 갱신 로직을 실행한다.
	 */
	override fun render() {
		super.render();
		update();
	}

	/**
	 * 매 프레임 게임 자체의 갱신 로직.
	 * 여기서는 제목 표시줄 내용을 갱신한다.
	 */
	private inline fun update() {
		val gameStateIndicator = when {
			GameManager.isPaused	-> "[일시 중지]"
			GameManager.isGameOver	-> "[게임 오버]"
			else					-> ""
		}.run {
			if(this.isBlank()) ""
			else " $this"
		};
		val roundInfo = if(currentRound > 0) " - 라운드 $currentRound" else "";
		val titleBarInfo = if(this.titleBarInfo.isBlank()) "" else " - $titleBarInfo";
		val titleBarStats = if(this.titleBarStats.isBlank()) "" else " / $titleBarStats";
		if(titleBarInfo.isEmpty())
			Gdx.graphics.setTitle("${title}${roundInfo}${titleBarStats}${gameStateIndicator}");
		else
			Gdx.graphics.setTitle("${title}${titleBarInfo}");
	}

	/**
	 * 현재 상태 정보를 제목 표시줄에 표시한다.
	 *
	 * @param info 상태
	 */
	fun setTitleBarInfo(info: String?) {
		titleBarInfo = info ?: "";
	}

	/**
	 * 현재 통계 정보를 제목 표시줄에 표시한다.
	 *
	 * @param info 통계
	 */
	fun setTitleBarStats(info: String?) {
		titleBarStats = info ?: "";
	}
}
