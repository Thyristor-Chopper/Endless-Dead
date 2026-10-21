package io.potatogun.endlessdead;

import io.potatogun.gdxhelper.Window;

/**
 * 제목 표시줄 제어기
 */
object TitleBar {
	/**
	 * 제목 표시줄 제목의 base
	 */
	@get:JvmStatic @set:JvmStatic var base: String? = null
		set(value) {
			field = value;
			updateTitle();
		};
	/**
	 * 제목 표시줄 제목에 표시할 정보
	 */
	@get:JvmStatic @set:JvmStatic var info: String? = null
		set(value) {
			field = value;
			updateTitle();
		};
	/**
	 * 제목 표시줄 제목에 표시할 통계
	 */
	@get:JvmStatic @set:JvmStatic var stats: String? = null
		set(value) {
			field = value;
			updateTitle();
		};

	/**
	 * 제목 표시줄 제목을 갱신한다.
	 */
	private inline fun updateTitle() {  // 세 군데만 쓰고 나 같으면 직접 복붙했을 수도 있어서 inline
		val base = this.base ?: "";
		val info = this.info?.let { " - $it" } ?: "";
		val stats = this.stats?.let { " / $it" } ?: "";
		Window.setTitle("${base}${info}${stats}");
	}
}
