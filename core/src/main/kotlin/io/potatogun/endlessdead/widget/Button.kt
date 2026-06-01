package io.potatogun.endlessdead.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.endlessdead.Input;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.Utils;

class Button(x: () -> Float, y: () -> Float, width: Float, height: Float = 20f, private var caption: String, private val onClick: () -> Unit = {}) : Widget(x, y, width, height) {
	private val button: NinePatch = Textures.button;
	private val buttonHover: NinePatch = Textures.buttonHover;
	private val buttonPressed: NinePatch = Textures.buttonPressed;
    private val font = BitmapFont();
	// private val accessKeyMatch = Regex("[&]([A-Za-z0-9])");
	// override val accessKey: Char? = accessKey ?: accessKeyMatch.find(caption)?.value?.get(1);
	// private val caption = caption.replaceFirst(accessKeyMatch, "$1");
	private var previouslyPressed = false;

	override fun draw(batch: SpriteBatch) {
		val x = this.x();
		val y = this.y();

		val mouseX = Gdx.input.getX();
		val mouseY = Gdx.graphics.height - Gdx.input.getY();
		val isHover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		val isPressed = Input.isButtonPressed(Input.LEFT_MOUSE);

		val toDraw: NinePatch =
			if(isPressed && isHover) {
				previouslyPressed = true;

				buttonPressed
			} else if(isHover) {
				fireClickEvent();

				buttonHover
			} else {
				fireClickEvent();

				button
			};

		toDraw.draw(batch, x, y, width, height);
		Utils.drawText(batch, font, caption, x, y + height / 2f + 7f, Color.BLACK, 1.0f, width, Align.center, true);
	}

	fun setCaption(caption: String) {
		this.caption = caption;
	}

	private inline fun fireClickEvent() {
		if(!previouslyPressed) return;
		onClick();
		previouslyPressed = false;
	}
}
