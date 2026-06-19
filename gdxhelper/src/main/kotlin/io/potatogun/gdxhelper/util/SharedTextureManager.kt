package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.gdxhelper.Utils;

abstract class SharedTextureManager {
	private val shared = mutableMapOf<String, Lazy<Texture>>();

	/**
	 * 텍스처를 등록한다(생성자에서만 사용할 것)
	 */
	protected fun register(id: String, path: String) {
		shared[id] = lazy { Utils.loadTexture(path) };
	}

	/**
	 * 지정한 이름의 공유 텍스처를 가져온다.
	 */
	fun getShared(id: String): Texture = shared[id]?.value ?: throw IllegalArgumentException("invalid shared texture ID");

	/**
	 * 공유 텍스처(여기서 정의된 텍스처)를 dispose한다.
	 *
	 * Screen이나 World가 아닌 Game에서 호출해야 한다.
	 */
	fun disposeShared() {
		for(texture in shared.values)
			if(texture.isInitialized())
				texture.value.dispose();
	}
}
