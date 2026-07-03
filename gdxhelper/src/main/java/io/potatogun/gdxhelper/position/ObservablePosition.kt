package io.potatogun.gdxhelper.position;

import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.function.BiConsumer;

import kotlin.properties.Delegates;

/**
 * 위치(평면좌표)를 저장하는 좌표변경을 감지할 수 있는 레코드이다.
 *
 * @param x 처음 X 좌표
 * @param y 처음 Y 좌표
 */
class ObservablePosition(x: Float, y: Float) : MutablePosition(x, y) {
	private val changeHandlers = GdxArray<(x: Float, y: Float) -> Unit>(false, 2);
	private val javaHandlerMap = ObjectMap<BiConsumer<Float, Float>, (x: Float, y: Float) -> Unit>(2);
	override var x: Float by Delegates.observable(x) { _, _, _ -> invokeObservers() };
	override var y: Float by Delegates.observable(y) { _, _, _ -> invokeObservers() };

	/**
	 * 값이 바뀔 때 콜백 함수를 지정한다 (자바에서 사용).
	 *
	 * @param handler 콜백
	 */
	fun addObserver(handler: BiConsumer<Float, Float>) {
		val ktHandler: (Float, Float) -> Unit = handler::accept;
		javaHandlerMap.put(handler, ktHandler);
		changeHandlers.add(ktHandler);
	}

	/**
	 * 값이 바뀔 때 콜백 함수를 지정한다.
	 *
	 * @param handler 콜백
	 */
	@JvmSynthetic fun addObserver(handler: (x: Float, y: Float) -> Unit) {
		changeHandlers.add(handler);
	}

	/**
	 * 값이 바뀔 때 콜백을 해제한다 (자바에서 사용).
	 *
	 * @param handler 해제할 콜백
	 */
	fun removeObserver(handler: BiConsumer<Float, Float>) {
		javaHandlerMap[handler]?.let {
			changeHandlers.removeValue(it, true);
			javaHandlerMap.remove(handler);
		};
	}

	/**
	 * 값이 바뀔 때 콜백을 해제한다.
	 *
	 * @param handler 해제할 콜백
	 */
	@JvmSynthetic fun removeObserver(handler: (x: Float, y: Float) -> Unit) {
		changeHandlers.removeValue(handler, true);
	}

	private inline fun invokeObservers() {
		for(i in 0 until changeHandlers.size)
			changeHandlers[i](x, y);
	}

	override fun copy(x: Float, y: Float): ObservablePosition = ObservablePosition(x, y);
}
