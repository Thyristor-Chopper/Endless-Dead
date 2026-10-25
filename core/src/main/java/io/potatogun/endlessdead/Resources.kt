package io.potatogun.endlessdead;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import io.potatogun.gdxhelper.util.SharedTextureManager;
import io.potatogun.gdxhelper.widget.Button;
import io.potatogun.gdxhelper.widget.ProgressBar;

/**
 * 텍스처나 그 외 그래픽 리소스 모음
 *
 * 여러 번, 많은 수의 인스턴스에서 쓰이는 개체의 공유된 텍스처는 여기에서 한 번만 불러온다.
 *
 * 여기에서 미리 정의된 텍스처는 Entity#dispose가 아닌 Game#dispose에서 정리된다.
 */
class Resources internal constructor() : Disposable {
	/**
	 * 게임의 공유 텍스처 모음
	 */
	@JvmField val textures = object : SharedTextureManager() {
		init {
			register("attacking_zombie", "entity/zombie_attacking.bmp");
			register("building", "entity/container/building.bmp");
			register("bullet", "entity/bullet.bmp");
			register("chest", "entity/container/chest.bmp");
			register("empty_building", "entity/container/building_empty.bmp");
			register("empty_chest", "entity/container/chest_empty.bmp");
			register("empty_vault", "entity/container/vault_empty.bmp");
			register("landmine", "entity/landmine.bmp");
			register("player_item_indicator", "entity/container/player_item.bmp");
			register("portal", "entity/portal.bmp");
			register("silver_bullet", "entity/bullet_silver.bmp");
			register("stream_zombie_summoner", "entity/stream_zombie_summoner.bmp");
			register("triggerman", "entity/triggerman.bmp");
			register("triggerman_summoner", "entity/triggerman_summoner.bmp");
			register("turret_friendly", "entity/turret_friendly.bmp");
			register("turret_hostile", "entity/turret_hostile.bmp");
			register("vault", "entity/container/vault.bmp");
			register("zombie", "entity/zombie.bmp");

			register("button", "widget/button.bmp", true);
			register("button_hover", "widget/button_hover.bmp", true);
			register("button_pressed", "widget/button_pressed.bmp", true);
			register("button_disabled", "widget/button_disabled.bmp", true);
			register("button_primary", "widget/button_primary.bmp", true);

			register("progress_bar", "widget/progress_bar.bmp", true);
			register("progress_fill", "widget/progress_chunk.bmp", true);
		}
	};
	/**
	 * 일반 단추(흰색이지만 Button 생성자의 Color 매개변수로 색 변환) 스킨
	 */
	@JvmField val button: Button.Skin;
	/**
	 * 기본 단추 스킨
	 */
	@JvmField val primaryButton: Button.Skin;
	/**
	 * 연속적 미터기 스킨
	 */
	@JvmField val smoothProgressBar: ProgressBar.Skin;
	/**
	 * 작은 연속적 미터기 스킨
	 */
	@JvmField val smallProgressBar: ProgressBar.Skin;
	/**
	 * 청크 미터기 스킨
	 */
	@JvmField val chunkedProgressBar: ProgressBar.Skin;
	/**
	 * 기본 글꼴
	 */
	@JvmField val font = BitmapFont();

	init {
		button = Button.Skin(NinePatch(textures.getShared("button"), 8, 8, 4, 5), NinePatch(textures.getShared("button_hover"), 8, 8, 4, 5), NinePatch(textures.getShared("button_pressed"), 8, 8, 4, 5), NinePatch(textures.getShared("button_disabled"), 8, 8, 4, 5), Color.WHITE, Color.WHITE, Color.WHITE, Color.LIGHT_GRAY, font);  // 어차피 게임 실행 시 단추가 나오므로 굳이 lazy로 할 필요 없음
		primaryButton = Button.Skin(NinePatch(textures.getShared("button_primary"), 8, 8, 4, 5), NinePatch(textures.getShared("button_hover"), 8, 8, 4, 5), NinePatch(textures.getShared("button_pressed"), 8, 8, 4, 5), NinePatch(textures.getShared("button_disabled"), 8, 8, 4, 5), Color.WHITE, Color.WHITE, Color.WHITE, Color.LIGHT_GRAY, font);

		val progressBar = NinePatch(textures.getShared("progress_bar"), 2, 2, 2, 2);
		val progressFill = textures.getShared("progress_fill");
		val smoothProgressFill = NinePatch(progressFill, 1, 1, 1, 1);
		smoothProgressBar = ProgressBar.Skin(progressBar, smoothProgressFill, 3f, 3f);
		smallProgressBar = ProgressBar.Skin(progressBar, smoothProgressFill, 2f, 2f);
		chunkedProgressBar = ProgressBar.Skin(progressBar, NinePatch(TextureRegion(progressFill, 1, 0, 1, progressFill.getHeight()), 0, 0, 1, 1), 3f, 3f, 6f, 2f);
	}

	override fun dispose() {
		textures.dispose();
		font.dispose();
	}
}
