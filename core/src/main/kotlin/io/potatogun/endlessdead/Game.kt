package io.potatogun.endlessdead;

import com.badlogic.gdx.Game as GdxGame;
import com.badlogic.gdx.Gdx;

import io.potatogun.endlessdead.Window;
import io.potatogun.endlessdead.screen.WorldViewer;

import java.lang.reflect.InvocationTargetException;

import kotlin.reflect.KClass;
import kotlin.reflect.full.primaryConstructor;

abstract class Game : GdxGame() {
	// 게임에 따라 월드별로 다른 뷰어를 쓸 수도 있으므로 여러 개를 쓸 수 있게 한다.
	// 예를 들어 좀비 월드는 체력 바가 있고... 체력 개념이 필요 없는 월드를 만든다면
	// 체력을 신경쓰지 않는 다른 뷰어를 만들 수도 있겠다.
	private val worldViewers = mutableListOf<WorldViewer>();

	/**
	 * 월드 뷰어를 등록한다. 이 게임에 속한 뷰어만 등록할 수 있고 같은 종류의 뷰어는 하나만 추가할 수 있다.
	 *
	 * @param	viewer	등록할 뷰어
	 * @return	등록된 뷰어
	 */
	fun addWorldViewer(viewer: WorldViewer): WorldViewer {
		if(viewer.game !== this)
			throw IllegalArgumentException("it is not allowed to add a world viewer that does not belong to this game instance");
		if(worldViewers.any { it::class == viewer::class })
			throw IllegalArgumentException("a world viewer of the same type is already present");
		worldViewers.add(viewer);
		return viewer;
	}

	/**
	 * 지정한 종류의 월드 뷰어를 반환하거나 없으면 새로 생성한다.
	 */
	fun <T : WorldViewer> getWorldViewer(viewerClass: KClass<T>): T {
		val viewer: WorldViewer? = worldViewers.firstOrNull { it::class == viewerClass };
		if(viewer != null) return viewer as T;

		val viewerToAdd = try {
			viewerClass.primaryConstructor!!.call(this)
		} catch(e: InvocationTargetException) {
			throw IllegalArgumentException("an instance of this type of world viewer has been randomly created elsewhere but has not been registered normally");
		} catch(e: Exception) {
			if(e is IllegalArgumentException || e is NullPointerException)
				throw IllegalArgumentException("the specified world viewer class does not have a valid standard constructor");
			else
				throw e;
		};
		worldViewers.add(viewerToAdd);
		return viewerToAdd;
	}

	fun getWorldViewers(): List<WorldViewer> = worldViewers.toList();

	/**
	 * Gdx.graphics.width를 매번 실수형으로 변환하는 오버헤드를 없애기 위해 창 크기를 캐시하고 크기가 바뀔 때만 업데이트한다.
	 */
	override fun resize(width: Int, height: Int) {
		Window.updateWindowDimensions();
		super.resize(width, height);
	}

	/**
	 * 자원을 정리한다.
	 */
	override fun dispose() {
		super.dispose();
		Textures.disposeShared();  // 공유 자원 정리
	}
}
