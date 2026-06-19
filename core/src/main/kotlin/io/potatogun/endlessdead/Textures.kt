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
	override val shared = mapOf<String, Lazy<Texture>>(
		"attacking_zombie" to lazy { Utils.loadTexture("entity/zombie_attacking.bmp") },
		"building" to lazy { Utils.loadTexture("entity/building.bmp") },
		"building_player_item" to lazy { Utils.loadTexture("entity/building_player_added.bmp") },
		"bullet" to lazy { Utils.loadTexture("entity/bullet.bmp") },
		"chest" to lazy { Utils.loadTexture("entity/chest.bmp") },
		"chest_player_item" to lazy { Utils.loadTexture("entity/chest_player_added.bmp") },
		"empty_building" to lazy { Utils.loadTexture("entity/building_empty.bmp") },
		"empty_chest" to lazy { Utils.loadTexture("entity/chest_empty.bmp") },
		"zombie" to lazy { Utils.loadTexture("entity/zombie.bmp") },
	);
}
