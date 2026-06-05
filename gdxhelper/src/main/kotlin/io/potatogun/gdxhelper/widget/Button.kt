package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.gdxhelper.HelperTextures;
import io.potatogun.gdxhelper.Input;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.widget.skin.ButtonSkin;

class Button(x: () -> Float, y: () -> Float, width: Float, height: Float = 25f, caption: String, accessKey: Char? = null, private val color: Color = Utils.rgb(216, 223, 239), private val skin: ButtonSkin = defaultSkin, private val onClick: () -> Unit = {}) : Widget(x, y, width, height) {
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

				skin.disabled
			} else if(isPressed && isHover) {
				previouslyPressed = true;

				skin.pressed
			} else if(isHover) {
				fireClickEvent();

				skin.hover
			} else {
				fireClickEvent();

				skin.normal
			};

		if(isEnabled) batch.color = color;
		toDraw.draw(batch, x, y, width, height);
		batch.color = Color.WHITE;
		Utils.drawText(batch, font, caption, x, y + height * 0.5f + 6f, fontColor, 1.0f, width, Align.center, true);

		detectAccessKeyPress();
	}

	// 바로 가기 키 처리
	private fun detectAccessKeyPress() {
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

	companion object {
		private val defaultSkin = ButtonSkin(HelperTextures.button, HelperTextures.buttonHover, HelperTextures.buttonPressed, HelperTextures.buttonDisabled);
	}
}
