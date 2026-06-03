package io.potatogun.gdxhelper;

import com.badlogic.gdx.Game as GdxGame;

import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.screen.WorldViewer;

import kotlin.reflect.KClass;

abstract class Game : GdxGame() {
	// 게임에 따라 월드별로 다른 뷰어를 쓸 수도 있으므로 여러 개를 쓸 수 있게 한다.
	// 예를 들어 좀비 월드는 체력 바가 있고... 체력 개념이 필요 없는 월드를 만든다면
	// 체력을 신경쓰지 않는 다른 뷰어를 만들 수도 있겠다.
	private val worldViewers = mutableMapOf<KClass<out WorldViewer>, WorldViewer>();

	/**
	 * 월드 뷰어를 등록한다. 이 게임에 속한 뷰어만 등록할 수 있고 같은 종류의 뷰어는 하나만 추가할 수 있다.
	 *
	 * @param	viewer	등록할 뷰어
	 * @return	등록된 뷰어
	 */
	fun addWorldViewer(viewer: WorldViewer): WorldViewer {
		if(viewer.game !== this)
			throw IllegalArgumentException("it is not allowed to add a world viewer that does not belong to this game instance");
		if(worldViewers.containsKey(viewer::class))
			throw IllegalArgumentException("a world viewer of the same type is already present");
		worldViewers[viewer::class] = viewer;
		return viewer;
	}

	// List#filterIsInstance<T>()같은 형태 어떻게 하는지 잘 모르겠어서 검색해봄
	/**
	 * 지정한 종류의 월드 뷰어를 반환하거나 없으면 새로 생성한다. 아래 함수와
	 * 기능은 같고 사용법만 다르다.
	 * getWorldViewer<원하는 뷰어 형>()
	 */
	inline fun <reified T : WorldViewer> getWorldViewer(): T = getWorldViewer(T::class);

	/**
	 * 지정한 종류의 월드 뷰어를 반환하거나 없으면 새로 생성한다.
	 * getWorldViewer(원하는 뷰어 형::class)
	 *
	 * @param viewerClass 원하는 뷰어 클래스
	 */
	fun <T : WorldViewer> getWorldViewer(viewerClass: KClass<T>): T {
		val viewer: WorldViewer? = worldViewers[viewerClass];
		if(viewer != null) return viewer as T;

		// 인자로 받은 클래스로 인스턴스를 생성하는 건 잘 모르겠어서 검색함... (오류 처리는 당연히 직접 했고)
		return try {
			// ?. 안 쓰고 !!. 하고 NullPointerException을 핸들링한 이유는
			//   아래의 IllegalArgumentException과 처리 방식이 어차피 같기 때문이다.
			//   오히려 여기서 ?.나 ?:을 쓰면 더 길어지고 반복되는 코드가 생긴다
			viewerClass.java.constructors.firstOrNull({ it.parameterTypes.size == 1 && Game::class.java.isAssignableFrom(it.parameterTypes[0]) })!!.newInstance(this) as T;
		} catch(e: Exception) {
			if(e is IllegalArgumentException || e is NullPointerException)
				throw IllegalArgumentException("the specified world viewer class does not use the standard constructor format");
			else
				throw e;
		};
	}

	// 자바의 콜렉션은 많이 써봐서 익숙함
	fun getWorldViewers(): Collection<WorldViewer> = worldViewers.values;

	/**
	 * Gdx.graphics.width를 매번 실수형으로 변환하는 오버헤드를 없애기 위해 창 크기를 캐시하고 크기가 바뀔 때만 업데이트한다.
	 */
	override fun resize(width: Int, height: Int) {
		Window.updateWindowDimensions();
		super.resize(width, height);
	}

	override fun dispose() {
		super.dispose();
		HelperTextures.disposeShared();
	}
}
