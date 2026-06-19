package io.potatogun.gdxhelper;

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
internal object HelperTextures : SharedTextureManager() {
	val button: NinePatch by lazy { NinePatch(getShared("button"), 12, 12, 7, 6) };
	val buttonHover: NinePatch by lazy { NinePatch(getShared("button_hover"), 12, 12, 7, 6) };
	val buttonPressed: NinePatch by lazy { NinePatch(getShared("button_pressed"), 12, 12, 7, 6) };
	val buttonDisabled: NinePatch by lazy { NinePatch(getShared("button_disabled"), 12, 12, 7, 6) };
	val progressBar: NinePatch by lazy { NinePatch(getShared("progress_bar"), 2, 2, 5, 6) };
	val progressChunkedFill: NinePatch by lazy { NinePatch(TextureRegion(getShared("progress_fill"), 1, 0, 1, getShared("progress_fill").getHeight()), 0, 0, 1, 1) };
	val progressSmoothFill: NinePatch by lazy { NinePatch(getShared("progress_fill"), 1, 1, 1, 1) };

	init {
		register("button", "widget/button.bmp");
		register("button_hover", "widget/button_hover.bmp");
		register("button_pressed", "widget/button_pressed.bmp");
		register("button_disabled", "widget/button_disabled.bmp");
		register("progress_bar", "widget/progress_bar.bmp");
		register("progress_fill", "widget/progress_chunk.bmp");
	}
}
