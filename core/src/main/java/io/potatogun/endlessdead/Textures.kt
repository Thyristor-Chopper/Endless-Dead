package io.potatogun.endlessdead;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.potatogun.gdxhelper.util.SharedTextureManager;
import io.potatogun.gdxhelper.widget.Button;
import io.potatogun.gdxhelper.widget.ProgressBar;

/**
 * 텍스처 관련 도우미
 *
 * 여러 번, 많은 수의 인스턴스에서 쓰이는 개체의 공유된 텍스처는 여기에서 한 번만 불러온다.
 *
 * 여기에서 미리 정의된 텍스처는 Entity#dispose가 아닌 Game#dispose에서 정리된다.
 */
object Textures : SharedTextureManager() {
	/**
	 * 기본 단추(흰색이지만 Button 생성자의 Color 매개변수로 색 변환) 스킨
	 */
	@JvmStatic val button: Button.Skin;
	/**
	 * 녹색 단추 스킨
	 */
	@JvmStatic val greenButton: Button.Skin;
	/**
	 * 연속적 미터기 스킨
	 */
	@JvmStatic val smoothProgressBar: ProgressBar.Skin;
	/**
	 * 청크 미터기 스킨
	 */
	@JvmStatic val chunkedProgressBar: ProgressBar.Skin;

	init {
		register("attacking_zombie", "entity/zombie_attacking.bmp");
		register("building", "entity/building.bmp");
		register("building_player_item", "entity/building_player_added.bmp");
		register("bullet", "entity/bullet.bmp");
		register("chest", "entity/chest.bmp");
		register("chest_player_item", "entity/chest_player_added.bmp");
		register("empty_building", "entity/building_empty.bmp");
		register("empty_chest", "entity/chest_empty.bmp");
		register("silver_bullet", "entity/bullet_silver.bmp");
		register("triggerman", "entity/triggerman.bmp");
		register("triggerman_summoner", "entity/triggerman_summoner.bmp");
		register("turret_friendly", "entity/turret_friendly.bmp");
		register("turret_hostile", "entity/turret_hostile.bmp");
		register("zombie", "entity/zombie.bmp");

		register("button", "widget/button.bmp", true);
		register("button_hover", "widget/button_hover.bmp", true);
		register("button_pressed", "widget/button_pressed.bmp", true);
		register("button_disabled", "widget/button_disabled.bmp", true);

		register("green_button", "widget/green_button.bmp", true);
		register("green_button_hover", "widget/green_button_hover.bmp", true);
		register("green_button_pressed", "widget/green_button_pressed.bmp", true);
		register("green_button_disabled", "widget/green_button_disabled.bmp", true);

		register("progress_bar", "widget/progress_bar.bmp", true);
		register("progress_fill", "widget/progress_chunk.bmp", true);

		button = Button.Skin(NinePatch(getShared("button"), 12, 12, 7, 6), NinePatch(getShared("button_hover"), 12, 12, 7, 6), NinePatch(getShared("button_pressed"), 12, 12, 7, 6), NinePatch(getShared("button_disabled"), 12, 12, 7, 6), Color.WHITE, Color.WHITE, Color.WHITE, Color.LIGHT_GRAY);  // 어차피 게임 실행 시 단추가 나오므로 굳이 lazy로 할 필요 없음
		greenButton = Button.Skin(NinePatch(getShared("green_button"), 12, 12, 7, 6), NinePatch(getShared("green_button_hover"), 12, 12, 7, 6), NinePatch(getShared("green_button_pressed"), 12, 12, 7, 6), NinePatch(getShared("green_button_disabled"), 12, 12, 7, 6), Color.WHITE, Color.WHITE, Color.WHITE, Color.LIGHT_GRAY);

		val progressBar = NinePatch(getShared("progress_bar"), 2, 2, 5, 6);
		val progressFill = getShared("progress_fill");
		smoothProgressBar = ProgressBar.Skin(progressBar, NinePatch(progressFill, 1, 1, 1, 1), 3f, 3f);
		chunkedProgressBar = ProgressBar.Skin(progressBar, NinePatch(TextureRegion(progressFill, 1, 0, 1, progressFill.getHeight()), 0, 0, 1, 1), 3f, 3f, 6f, 2f);
	}
}
