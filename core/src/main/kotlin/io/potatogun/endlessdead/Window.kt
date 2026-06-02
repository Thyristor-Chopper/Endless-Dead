package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;

import kotlin.properties.Delegates;

/**
 * 게임 화면(창)에 대한 것들
 */
object Window {
	val width: Float
		inline get() = Gdx.graphics.width.toFloat();
	val height: Float
		inline get() = Gdx.graphics.height.toFloat();
	var titleBarInfo: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };
	var titleBarStats: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };

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
}
