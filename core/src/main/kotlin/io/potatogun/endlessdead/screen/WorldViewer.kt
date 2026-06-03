package io.potatogun.endlessdead.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Game;
import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Window;
import io.potatogun.endlessdead.world.World;

import kotlin.reflect.KClass;

/**
 * 월드를 불러오고 월드를 화면에 프로젝션해주는 스크린이다.
 *
 * 상속받아서 더 확장된 월드 뷰어를 만들어도 되지만
 * 동일한 종류의 월드 뷰어는 한 게임 인스턴스당 하나만 생성할 수 있다.
 *
 * 월드 뷰어는 WorldViewer(Game) 생성자를 직접 호출하여 만들 수 있으며
 *   Game#getWorldViewer를 통해 원하는 뷰어 종류를 전달하여 없으면 자동으로 생성하게 할 수도 있지만
 *   Game 매개변수 하나만을 받는 뷰어만 지원된다.
 *
 * 생성할 경우 자동으로 게임의 worldViewers에 등록된다.
 */
open class WorldViewer(game: Game) : Screen(game) {
	// 표시할 월드
	private var world: World? = null;
	// 자막 타이머 관련 필드들. 우리가 만든 Timer 객체와 달리 일정 시간 간격으로 '계속' 실행하는 그런 게 아니기 때문에 따로 관리.
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;

	init {
		game.addWorldViewer(this);
	}

	/**
	 * 지정한 월드를 연다.
	 *
	 * @param world					불러올 월드
	 * @param disposePreviousWorld	기존 월드의 자원을 정리할지의 여부
	 */
	fun loadWorld(world: World, disposePreviousWorld: Boolean = false) {
		if(world.game !== this.game)  // 안전을 위해 동일 게임 인스턴스에 속한 월드만 불러오게 함
			throw IllegalArgumentException("WorldViewer can only project worlds that belong to the same game instance of this viewer");
		val previousWorld: World? = this.world;
		this.world = world;
		world.updateCamera();
		if(disposePreviousWorld) Gdx.app.postRunnable { previousWorld?.dispose() };
	}

	/**
	 * 현재 보여주고 있는 월드를 닫는다.
	 *
	 * @param 	dispose	월드의 자원을 정리할지의 여부
	 * @return	성공 여부
	 */
	fun unloadWorld(dispose: Boolean = false): Boolean {
		val currentWorld: World? = world;
		if(currentWorld == null) return false;
		world = null;
		if(dispose) Gdx.app.postRunnable { currentWorld.dispose() };
		return true;
	}

	/**
	 * 현재 보여주고 있는 월드를 반환한다.
	 */
	fun getProjectingWorld(): World? = world;

	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		world?.onResize(width, height);
	}

	/**
	 * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경'은 이 안에서.
	 *
	 * 상태별로 해야 할 일이 완전히 다르므로 when으로 분기한다.
	 * (입력 처리가 render()가 아닌 update() 에 있는 이유:
	 *  '로직과 그리기의 분리' — render는 매 프레임 그리는 일에만 집중하고,
	 *  상태 변화·입력은 update가 책임진다.)
	 */
	override fun update(delta: Float) {
		super.update(delta);

		when {
            GameManager.isPlaying	-> updateInPlay(delta);
            GameManager.isPaused	-> updatePaused();
            GameManager.isGameOver	-> updateGameOver();
        }
	}

    /**
	 * PLAYING 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크.
	 *
	 * update에서만 한 번 쓰이기 때문에 inline이다.
	 */
    open fun updateInPlay(delta: Float) {
		// 월드 갱신
		world?.update(delta);

		// 자막 만료 타이머 갱신
		if(subtitlesTimer > 0f)
			subtitlesTimer -= delta;
    }

	/**
	 * 일시 정지 상태에서 매 프레임 로직
	 *
	 * 객체 업데이트(super.update)나 타이머(spawner.tick)를 호출하지 않음.
	 *   세상이 그대로 멈춰있는 상태가 됨
	 */
	open fun updatePaused() {}

    /**
	 * 게임 오버 상태에서 매 프레임 처리
	 */
    open fun updateGameOver() {}

	/**
	 * 일반적으로 월드가 아닌 뷰어 자체의 배경은 없다(그려봤자 월드의 배경이 반투명하지 않는 이상 가려질 것이다).
	 */
	override fun drawBackground() {}

	override fun drawElements() {
		// 월드 관련 처리...
		batch.end();  // 월드의 그리기 배치를 처리하기 전에 화면 자체의 배치를 잠시 중지.
		// 왜 World#render를 Screen#render가 아닌 drawElements에서 하냐고 묻는다면
		//   월드 뷰어 스크린 입장에서 월드는 이 스크린의 요소 중 하나일 뿐이기 때문이다.
		world?.render();
		batch.begin();  // 월드의 그리기가 끝나면 화면의 그리기 배치를 다시 시작

		// 자막이 있으면 표시
		if(subtitlesTimer > 0f) subtitlesMessage?.let {
			drawText(
				text = it,
				x = 0f,
				y = 20f,
				color = subtitlesColor,
				scale = 1.0f,
				width = Window.width,
				align = Align.center,
				skipBatch = true
			);
		};
	}

	/**
	 * 화면 하단에 자막을 표시한다.
	 *
	 * @param message	표시할 내용
	 * @param duration	표시 시간(초)
	 * @param color		글자 색
	 */
	fun drawSubtitles(message: String, duration: Int = 3, color: Color = Color.WHITE) {
		subtitlesTimer = duration.toFloat();
		subtitlesMessage = message;
		subtitlesColor = color;
	}

	override fun dispose() {
		super.dispose();
		world?.dispose();
	}
}
