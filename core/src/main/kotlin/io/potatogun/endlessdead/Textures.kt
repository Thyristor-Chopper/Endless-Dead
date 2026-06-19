package io.potatogun.endlessdead;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.gdxhelper.SharedTextureManager;
import io.potatogun.gdxhelper.Utils;

/**
 * 텍스처 관련 도우미
 *
 * 여러 번, 많은 수의 인스턴스에서 쓰이는 개체의 공유된 텍스처는 여기에서 한 번만 불러온다.
 *
 * 여기에서 미리 정의된 텍스처는 Entity#dispose가 아닌 Game#dispose에서 정리된다.
 */
object Textures : SharedTextureManager() {
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
	}
}
