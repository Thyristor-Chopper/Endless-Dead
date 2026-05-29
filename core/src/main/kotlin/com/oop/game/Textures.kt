package com.oop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

object Textures {
	val player: Texture by lazy { load("player.bmp") };
	val playerWithGun: Texture by lazy { load("player_holding_gun.bmp") };
	val bullet: Texture by lazy { load("bullet.bmp") };
	val zombie: Texture by lazy { load("zombie.bmp") };
	val chest: Texture by lazy { load("chest.bmp") };
	val emptyChest: Texture by lazy { load("chest_empty.bmp") };
	val chestPlayerItem: Texture by lazy { load("chest_player_added.bmp") };
	val building: Texture by lazy { load("building.bmp") };
	val emptyBuilding: Texture by lazy { load("building_empty.bmp") };
	val buildingPlayerItem: Texture by lazy { load("building_player_added.bmp") };
	val progressBarTexture: Texture by lazy { load("progress_bar.bmp") };
	val progressChunkTexture: Texture by lazy { load("progress_chunk.bmp") };
	val progressBar: NinePatch by lazy { NinePatch(progressBarTexture, 2, 2, 5, 6) };
	val progressBarIndicator: NinePatch by lazy { NinePatch(progressChunkTexture, 1, 1, 1, 1) };
	val progressBarChunk: NinePatch by lazy { NinePatch(TextureRegion(progressChunkTexture, 1, 0, 1, 12), 0, 0, 1, 1) };

	inline fun load(path: String): Texture = Texture(Gdx.files.internal(path));
}
