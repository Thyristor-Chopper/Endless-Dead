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

class Button(x: () -> Float, y: () -> Float, width: Float, height: Float = 25f, caption: String, accessKey: Char? = null, private val onClick: () -> Unit = {}) : Widget(x, y, width, height) {
	private val button: NinePatch = Textures.button;
	private val buttonHover: NinePatch = Textures.buttonHover;
	private val buttonPressed: NinePatch = Textures.buttonPressed;
	private val buttonDisabled: NinePatch = Textures.buttonDisabled;
    private val font = BitmapFont();
	private val accessKey: Char?;
	private val caption: String;
	private var previouslyPressed = false;
	private var isEnabled = true;

	init {
		val accessKeyMatch = Regex("[&]([A-Za-z])");
		this.accessKey = (accessKey ?: accessKeyMatch.find(caption)?.value?.get(1))?.uppercaseChar();
		this.caption = caption.replaceFirst(accessKeyMatch, "$1");
	}

	override fun draw(batch: SpriteBatch) {
		val x = this.x();
		val y = this.y();

		val mouseX = Input.mouseX;
		val mouseY = Gdx.graphics.height - Input.mouseY;
		val isHover = isEnabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		val isPressed = isEnabled && Input.isButtonPressed(Input.LEFT_MOUSE);
		val fontColor = if(!isEnabled) Color.LIGHT_GRAY else Color.WHITE;

		val toDraw: NinePatch =
			if(!isEnabled) {
				previouslyPressed = false;

				buttonDisabled
			} else if(isPressed && isHover) {
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
		Utils.drawText(batch, font, caption, x, y + height * 0.5f + 6f, fontColor, 1.0f, width, Align.center, true);

		detectAccessKeyPress();
	}

	// 바로 가기 키 처리
	fun detectAccessKeyPress() {
		if(accessKey != null && Input.isKeyJustPressed(accessKey.code - 36))
			onClick();
	}

	fun enable() {
		isEnabled = true;
	}

	fun disable() {
		isEnabled = false;
	}

	private inline fun fireClickEvent() {
		if(!previouslyPressed) return;
		onClick();
		previouslyPressed = false;
	}
}
