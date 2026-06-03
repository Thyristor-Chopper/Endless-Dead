package io.potatogun.gdxhelper.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import io.potatogun.gdxhelper.Game;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.world.World;

/**
 * 월드를 불러오고 월드를 화면에 프로젝션해주는 스크린이다.
 *
 * 상속받아서 hud가 포함된 더 확장된 월드 뷰어를 만들 수 있지만
 *   가급적이면 같은 종류의 뷰어는 하나만 생성하는 것을 권한다.
 *   (기존엔 두 개 이상의 인스턴스를 생성하면 오류가 나게 했지만 굳이 오류를
 *    낼 정도는 아닌 것 같다.)
 *
 * 생성할 경우 자동으로 게임의 worldViewers에 등록된다.
 */
open class WorldViewer : Screen() {
	/**
	 * 현재 보여주고 있는 월드를 반환한다.
	 */
	var projectingWorld: World? = null
		private set;
	// 자막 타이머 관련 필드들. 우리가 만든 Timer 객체와 달리 일정 시간 간격으로 '계속' 실행하는 그런 게 아니기 때문에 따로 관리.
	private var subtitlesTimer = 0f;
	private var subtitlesMessage: String? = null;
	private var subtitlesColor = Color.WHITE;

	init {
		instances.add(this);
	}

	/**
	 * 지정한 월드를 연다.
	 *
	 * @param world					불러올 월드
	 * @param disposePreviousWorld	기존 월드의 자원을 정리할지의 여부
	 */
	fun loadWorld(world: World, disposePreviousWorld: Boolean = false) {
		if(projectingWorld === world) return;  // 아무 작업도 할 필요 없음
		if(instances.any { it.projectingWorld === world })
			throw IllegalArgumentException("another viewer is already projecting that world");
		val previousWorld: World? = projectingWorld;
		projectingWorld = world;
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
		val currentWorld: World? = projectingWorld;
		if(currentWorld == null) return false;
		projectingWorld = null;
		if(dispose) Gdx.app.postRunnable { currentWorld.dispose() };
		return true;
	}

	override fun resize(width: Int, height: Int) {
		super.resize(width, height);
		projectingWorld?.onResize(width, height);
	}

	/**
	 * 월드와 자막 만료 타이머 갱신
	 */
	override fun update(delta: Float) {
		// 월드 갱신
		projectingWorld?.update(delta);

		// 자막 만료 타이머 갱신
		if(subtitlesTimer > 0f)
			subtitlesTimer -= delta;
	}

	/**
	 * 일반적으로 월드가 아닌 뷰어 자체의 배경은 없다(그려봤자 월드의 배경이 반투명하지 않는 이상 가려질 것이다).
	 */
	override fun drawBackground() {}

	override fun drawElements() {
		// 월드 관련 처리...
		batch.end();  // 월드의 그리기 배치를 처리하기 전에 화면 자체의 배치를 잠시 중지.
		// 왜 World#render를 Screen#render가 아닌 drawElements에서 하냐고 묻는다면
		//   월드 뷰어 스크린 입장에서 월드는 이 스크린의 요소 중 하나일 뿐이기 때문이다.
		projectingWorld?.render();
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
		projectingWorld?.dispose();
	}

	companion object {
		// 생성된 모든 인스턴스를 관리하는 목록이다. 생성자에서 자동으로 추가한다. 누가 설마 자바 unsafe의 allocateInstance를 쓰진 않겠지
		private val instances = mutableListOf<WorldViewer>();

		fun getViewerByWorld(world: World): WorldViewer? = instances.firstOrNull { it.projectingWorld === world };
	}
}
