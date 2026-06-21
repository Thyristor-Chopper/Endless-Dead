package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.gdxhelper.Utils;

/**
 * 공유 자원 관리자
 */
abstract class SharedTextureManager {
	private val shared = mutableMapOf<String, Lazy<Texture>>();

	/**
	 * 텍스처를 등록한다(생성자에서 사용)
	 *
	 * @param id   텍스처 식별자
	 * @param path 텍스처 경로
	 */
	protected fun register(id: String, path: String) {
		shared[id] = lazy { Utils.loadTexture(path) };
	}

	/**
	 * 지정한 이름의 공유 텍스처를 가져온다.
	 *
	 * @param id 텍스처 식별자
	 * @return   지정한 식별자의 텍스처
	 * @throws IllegalArgumentException 지정한 식별자의 텍스처가 없는 경우
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
