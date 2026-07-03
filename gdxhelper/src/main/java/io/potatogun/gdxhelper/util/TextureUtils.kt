package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.gdxhelper.util.SharedTextureManager;

/**
 * 텍스처 관련 함수들
 */
object TextureUtils {
	/**
	 * 지정한 화일 이름의 텍스처를 가져온다.
	 *
	 * @param path 화일 이름
	 * @return     불러온 텍스처 객체
	 */
	@JvmStatic inline fun loadTexture(path: String): Texture = Texture(Gdx.files.internal("assets/textures/$path"));

	/**
	 * 공유 자원이 아닌 경우에만 비디오 카드 메모리에서 정리한다.
	 *
	 * @param texture 해제할 텍스처
	 * @return        성공 여부
	 */
	@JvmStatic inline fun safeDispose(texture: Texture): Boolean {
		if(!SharedTextureManager.isSharedTexture(texture)) {
			texture.dispose();
			return true;
		}
		return false;
	}
}
