package io.potatogun.gdxhelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

abstract class TextureManager {
	protected abstract val shared: Map<String, Lazy<Texture>>;

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

	companion object {
		/**
		 * 지정한 화일 이름의 텍스처를 가져온다.
		 *
		 * @param path 화일 이름
		 */
		@JvmStatic inline fun loadTexture(path: String): Texture = Texture(Gdx.files.internal("assets/textures/$path"));
	}
}
