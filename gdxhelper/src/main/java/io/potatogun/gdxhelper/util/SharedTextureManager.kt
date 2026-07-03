package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

import io.potatogun.gdxhelper.collections.weakMutableSetOf;
import io.potatogun.gdxhelper.util.TextureUtils;

/**
 * 공유 자원 관리자
 */
abstract class SharedTextureManager {
	private val shared = ObjectMap<String, Lazy<Texture>>();

	/**
	 * 텍스처를 등록한다.
	 *
	 * @param id   텍스처 식별자
	 * @param path 텍스처 경로
	 */
	protected fun register(id: String, path: String) {
		shared.put(id, lazy {
			val texture = TextureUtils.loadTexture(path);
			sharedTextures.add(texture);

			/* return */ texture
		});
	}

	/**
	 * 텍스처를 등록한다.
	 *
	 * @param id      텍스처 식별자
	 * @param texture 텍스처
	 */
	protected fun register(id: String, texture: Texture) {
		sharedTextures.add(texture);
		shared.put(id, lazyOf(texture));
	}

	/**
	 * 지정한 이름의 공유 텍스처를 가져온다.
	 *
	 * @param id 텍스처 식별자
	 * @return   지정한 식별자의 텍스처
	 * @throws NoSuchElementException 지정한 식별자의 텍스처가 없는 경우
	 */
	fun getShared(id: String): Texture = shared[id]?.value ?: throw NoSuchElementException("invalid shared texture ID");

	/**
	 * 공유 텍스처(여기서 정의된 텍스처)를 dispose한다.
	 *
	 * Screen이나 World가 아닌 Game에서 호출해야 한다.
	 */
	fun disposeShared() {
		val iterator = shared.values().iterator();
		while(iterator.hasNext()) {
			val texture = iterator.next();
			if(texture.isInitialized())
				texture.value.dispose();
		}
	}

	companion object {
		private val sharedTextures = weakMutableSetOf<Texture>();

		@JvmStatic fun isSharedTexture(texture: Texture): Boolean = sharedTextures.contains(texture);
	}
}
