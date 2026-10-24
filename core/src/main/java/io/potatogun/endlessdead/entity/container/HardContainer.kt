package io.potatogun.endlessdead.entity.container;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.potatogun.endlessdead.Resources;
import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.item.Item;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.widget.ProgressBar;
import io.potatogun.gdxhelper.world.World;

/**
 * 여는 데 시간이 걸리는 아이템 보관함
 *
 * @param    world        개체가 속한 세계
 * @param    name         개체 표시 이름
 * @param    x            개체의 X 위치
 * @param    y            개체의 Y 위치
 * @param    width        개체 너비
 * @param    height       개체 높이
 * @param    texture      개체 텍스처
 * @param    emptyTexture 상자가 비어 있을 때 사용할 텍스처
 * @param    initialItem  처음 들어있는 아이템
 * @property openDuration 여는 데 걸리는 시간
 */
abstract class HardContainer(world: World? = null, name: String, x: Float = 0f, y: Float = 0f, width: Float, height: Float, texture: Texture?, emptyTexture: Texture? = null, initialItem: Item? = null, private val openDuration: Float): Container(world, name, x, y, width, height, texture, emptyTexture, initialItem) {
	@get:JvmName("isOpening") val opening: Boolean
		get() = (!opened && openTimer > 0f);
	private var openTimer = 0f;
	@get:JvmName("isOpened") var opened = false
		private set;
	private var openCallback: Runnable? = null;
	private val openingProgressBar = ProgressBar({ x - 18f + originOffsetX }, { y + height * 0.5f + originOffsetY }, { 36f }, { 8f }, skin = Resources.smallProgressBar, color = Color.SLATE);
	private var opener: Entity? = null;

	init {
		if(openDuration <= 0f)
			throw IllegalArgumentException("invalid open duration");
	}

	override fun update(delta: Float) {
		super.update(delta);

		if(opening) {
			if(!(opener?.collidesWith(this) ?: true)) {  // 여는 이가 자리를 벗어나면 열기 취소
				openTimer = 0f;
				opener = null;
				openCallback = null;
			} else {
				openTimer -= delta;
				if(openTimer <= 0f) {
					openTimer = 0f;
					opened = true;
					opener = null;
					openCallback?.let {
						it.run();
						openCallback = null;
					};
				}
				openingProgressBar.value = 1f - openTimer / openDuration;
			}
		}
	}

	override fun draw(batch: SpriteBatch) {
		super.draw(batch);

		if(opening)
			openingProgressBar.draw(batch);
	}

	@JvmOverloads fun open(opener: Entity? = null, callback: Runnable? = null) {
		if(opened || opening) return;
		openTimer = openDuration;
		openCallback = callback;
		this.opener = opener;
	}

	override fun takeItem(taker: InventoryHolder): Item? {
		if(!opened) return null;
		return super.takeItem(taker);
	}

	override fun putItem(item: Item, isPlayerItem: Boolean): Boolean {
		if(opening) return false;
		if(!isPlayerItem)
			opened = false;
		return super.putItem(item, isPlayerItem);
	}

	override fun dispose() {
		super.dispose();
		openingProgressBar.dispose();
	}
}
