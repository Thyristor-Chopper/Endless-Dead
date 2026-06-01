package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 텍스처 관련 도우미
 *
 * 여러 번, 많은 수의 인스턴스에서 쓰이는 개체의 공유된 텍스처는 여기에서 한 번만 불러온다.
 *
 * 여기에서 미리 정의된 텍스처는 Entity#dispose가 아닌 Game#dispose에서 정리된다.
 */
object Textures {
	private val shared = mapOf<String, Lazy<Texture>>(
		"attacking_zombie" to lazy { loadTexture("entity/zombie_attacking.bmp") },
		"building" to lazy { loadTexture("entity/building.bmp") },
		"building_player_item" to lazy { loadTexture("entity/building_player_added.bmp") },
		"bullet" to lazy { loadTexture("entity/bullet.bmp") },
		"button" to lazy { loadTexture("widget/button.bmp") },
		"button_hover" to lazy { loadTexture("widget/button_hover.bmp") },
		"button_pressed" to lazy { loadTexture("widget/button_pressed.bmp") },
		"button_disabled" to lazy { loadTexture("widget/button_disabled.bmp") },
		"chest" to lazy { loadTexture("entity/chest.bmp") },
		"chest_player_item" to lazy { loadTexture("entity/chest_player_added.bmp") },
		"empty_building" to lazy { loadTexture("entity/building_empty.bmp") },
		"empty_chest" to lazy { loadTexture("entity/chest_empty.bmp") },
		"progress_bar" to lazy { loadTexture("widget/progress_bar.bmp") },
		"progress_fill" to lazy { loadTexture("widget/progress_chunk.bmp") },
		"zombie" to lazy { loadTexture("entity/zombie.bmp") },
	);
	val button: NinePatch by lazy { NinePatch(shared["button"]!!.value, 12, 12, 7, 6) };
	val buttonHover: NinePatch by lazy { NinePatch(shared["button_hover"]!!.value, 12, 12, 7, 6) };
	val buttonPressed: NinePatch by lazy { NinePatch(shared["button_pressed"]!!.value, 12, 12, 7, 6) };
	val buttonDisabled: NinePatch by lazy { NinePatch(shared["button_disabled"]!!.value, 12, 12, 7, 6) };
	val progressBar: NinePatch by lazy { NinePatch(shared["progress_bar"]!!.value, 2, 2, 5, 6) };
	val progressChunkedFill: NinePatch by lazy { NinePatch(TextureRegion(shared["progress_fill"]!!.value, 1, 0, 1, shared["progress_fill"]!!.value.getHeight()), 0, 0, 1, 1) };
	val progressSmoothFill: NinePatch by lazy { NinePatch(shared["progress_fill"]!!.value, 1, 1, 1, 1) };

	/**
	 * 지정한 이름의 공유 텍스처를 가져온다.
	 */
	fun getShared(id: String): Texture = shared[id]?.value ?: throw IllegalArgumentException("invalid shared texture ID");

	/**
	 * 지정한 화일 이름의 텍스처를 가져온다.
	 *
	 * @param path 화일 이름
	 */
	inline fun loadTexture(path: String): Texture = Texture(Gdx.files.internal("assets/textures/$path"));

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
