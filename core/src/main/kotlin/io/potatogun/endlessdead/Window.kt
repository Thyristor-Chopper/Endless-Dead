package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import kotlin.properties.Delegates;

/**
 * 게임 화면(창)에 대한 것들
 */
object Window {
	// Gdx.graphics.width를 매번 실수형으로 변환하는 오버헤드를 없애기 위해 캐시하기
	var width = 0f  // lateinit이 불가하여 0으로 초기화
		private set;
	var height = 0f
		private set;
	// 부동 소수점 나눗셈은 느리기 때문에 창 크기의 절반도 캐시
	var halfWidth = 0f
		private set;
	var halfHeight = 0f
		private set;
	var titleBarInfo: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };
	var titleBarStats: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };

	init {
		updateWindowDimensions();
	}

	/**
	 * 창 제목을 직접 변경한다.
	 */
	inline fun setTitle(title: String) {
		Gdx.graphics.setTitle(title);
	}

	private fun updateTitle() {
		val titleBarInfo = this.titleBarInfo?.let { " - $it" } ?: "";
		val titleBarStats = this.titleBarStats?.let { " / $it" } ?: "";
		Window.setTitle("${Constants.GAME_TITLE}${titleBarInfo}${titleBarStats}");
	}

	/**
	 * 창 크기 캐시를 최신화한다.
	 */
	internal fun updateWindowDimensions() {
		val floatWidth = Gdx.graphics.width.toFloat();
		val floatHeight = Gdx.graphics.height.toFloat();

		width = floatWidth;
		height = floatHeight;

		// 부동 소수점 나눗셈은 느리기 때문에 창 크기의 절반도 캐시
		Window.halfWidth = floatWidth / 2f;
		Window.halfHeight = floatHeight / 2f;
	}
}
