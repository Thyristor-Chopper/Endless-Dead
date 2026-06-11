package io.potatogun.gdxhelper;

import com.badlogic.gdx.Gdx;

import kotlin.properties.Delegates;

/**
 * 게임 화면(창)에 대한 것들
 */
object Window {
	// 창 크기 캐시
	//   전에는 'val width get() = Gdx.graphics.width.toFloat()'로 구현했다.
	//   이에 관한 고찰을 적어보자면...
	//   일단 private set이 있기 때문에 @JvmField 적용이 불가능하여 getter가 생겨 컴파일 타임에서는 어쨌거나 함수 호출이 있다.
	//   그리고 Gdx.graphics.getWidth()에서 Graphics 클래스가 자체적으로 길이를 캐시한다는 말이 있지만
	//   libGDX의 소스코드를 읽어봤지만 잘은 모르겠다 - https://github.com/libgdx/libgdx/blob/f9af1f6273e04da84eaffebbb782d083441634b0/backends/gdx-backend-lwjgl3/src/com/badlogic/gdx/backends/lwjgl3/Lwjgl3Graphics.java#L21
	//   gdx 내부적으로 캐시하지 않고 매번 계산할 가능성도 염두하여 캐시하긴 한다.
	//   참고로 Int#toFloat()는 실제 함수 호출으로 구현되지는 않고 자바의 '(float) 정수' 형태로 변환된다.
	//   그래서 toFloat '함수' 호출 오버헤드는 없다. (이 부분도 빌드 후 디컴파일된 자바 코드로 직접 확인함)
	@JvmStatic var width = 0f  // lateinit이 불가하여 0으로 초기화
		private set;
	@JvmStatic var height = 0f
		private set;
	private var titleBarBase: String? by Delegates.observable("Game") { _, _, _ -> updateTitle() };
	@JvmStatic var titleBarInfo: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };
	@JvmStatic var titleBarStats: String? by Delegates.observable(null) { _, _, _ -> updateTitle() };

	init {
		updateWindowDimensions();
	}

	/**
	 * 창 제목을 직접 변경한다.
	 */
	@JvmStatic inline fun setTitle(title: String) {
		Gdx.graphics.setTitle(title);
	}

	@JvmStatic fun setBaseTitle(value: String) {
		titleBarBase = value;
	}

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
		val floatWidth = Gdx.graphics.width.toFloat();
		val floatHeight = Gdx.graphics.height.toFloat();

		width = floatWidth;
		height = floatHeight;
	}
}
