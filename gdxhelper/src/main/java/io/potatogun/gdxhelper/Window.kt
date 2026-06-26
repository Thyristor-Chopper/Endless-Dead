package io.potatogun.gdxhelper;

import com.badlogic.gdx.Gdx;

import kotlin.properties.Delegates;

/**
 * 게임 화면(창)에 대한 것들
 */
object Window {
	// 창 크기 캐시
	/**
	 * 현재 창의 너비
	 */
	@JvmStatic var width = 0f  // lateinit이 불가하여 0으로 초기화
		private set;
	/**
	 * 현재 창의 높이
	 */
	@JvmStatic var height = 0f
		private set;
	/**
	 * 제목 표시줄 제목의 base
	 */
	private var titleBarBase: String? by Delegates.observable("Game") { _, _, _ -> updateTitle() };
	/**
	 * 제목 표시줄 제목에 표시할 상태 정보
	 */
	@JvmStatic var titleBarInfo: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };
	/**
	 * 제목 표시줄 제목에 표시할 통계 정보
	 */
	@JvmStatic var titleBarStats: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };

	init {
		updateWindowDimensions();
	}

	/**
	 * 창 제목을 직접 변경한다.
	 * @param title 전체 제목
	 */
	@JvmStatic inline fun setTitle(title: String) {
		Gdx.graphics.setTitle(title);
	}

	/**
	 * 제목 표시줄 제목의 base를 지정한다.
	 *
	 * @param value base title
	 */
	@JvmStatic fun setBaseTitle(value: String) {
		titleBarBase = value;
	}

	/**
	 * 제목 표시줄 제목을 갱신한다.
	 */
	private fun updateTitle() {
		val titleBarInfo = this.titleBarInfo?.let { " - $it" } ?: "";
		val titleBarStats = this.titleBarStats?.let { " / $it" } ?: "";
		setTitle("${titleBarBase}${titleBarInfo}${titleBarStats}");
	}

	/**
	 * 창 크기 캐시를 최신화한다.
	 *
	 * Game#resize에서 width, height 인자를 받아오면 Gdx.graphics.getWidth() 콜 오버헤드마저 없어져서
	 *   더 극단적인 최적화가 가능하지만 이렇게 하면 다른 클래스에서 이상한 너비 높이 값으로
	 *   updateWindowDimensions을 호출할 위험이 있다. 그래도 최적화와 안전의 균형은
	 *   지키는 게 나을 수도.
	 */
	internal fun updateWindowDimensions() {
		width = Gdx.graphics.width.toFloat();
		height = Gdx.graphics.height.toFloat();
	}
}
