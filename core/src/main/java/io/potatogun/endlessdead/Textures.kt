package io.potatogun.endlessdead;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;

import io.potatogun.gdxhelper.util.SharedTextureManager;
import io.potatogun.gdxhelper.widget.Button;

/**
 * 텍스처 관련 도우미
 *
 * 여러 번, 많은 수의 인스턴스에서 쓰이는 개체의 공유된 텍스처는 여기에서 한 번만 불러온다.
 *
 * 여기에서 미리 정의된 텍스처는 Entity#dispose가 아닌 Game#dispose에서 정리된다.
 */
object Textures : SharedTextureManager() {
	/**
	 * 녹색 단추 스킨
	 */
	val greenButton: Button.Skin by lazy { Button.Skin(NinePatch(getShared("green_button"), 12, 12, 7, 6), NinePatch(getShared("green_button_hover"), 12, 12, 7, 6), NinePatch(getShared("green_button_pressed"), 12, 12, 7, 6), NinePatch(getShared("green_button_disabled"), 12, 12, 7, 6), Color.WHITE, Color.LIGHT_GRAY) };

	init {
		register("attacking_zombie", "entity/zombie_attacking.bmp");
		register("building", "entity/building.bmp");
		register("building_player_item", "entity/building_player_added.bmp");
		register("bullet", "entity/bullet.bmp");
		register("chest", "entity/chest.bmp");
		register("chest_player_item", "entity/chest_player_added.bmp");
		register("empty_building", "entity/building_empty.bmp");
		register("empty_chest", "entity/chest_empty.bmp");
		register("zombie", "entity/zombie.bmp");

		register("green_button", "widget/green_button.bmp");
		register("green_button_hover", "widget/green_button_hover.bmp");
		register("green_button_pressed", "widget/green_button_pressed.bmp");
		register("green_button_disabled", "widget/green_button_disabled.bmp");

		register("default_item", "item/default.bmp");
	}
}
