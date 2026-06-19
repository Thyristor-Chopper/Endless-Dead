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

/**
 * 단추
 *
 * x과 y 위치는 screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 *
 * @param x			X 좌표 계산 함수
 * @param y			Y 좌표 계산 함수
 * @param width		단추 너비
 * @param height	단추 높이
 * @param caption	단추 라벨
 * @param accessKey	단추 바로 가기 ('니모닉')
 * @param color		단추의 색
 * @param skin		단추의 스킨(텍스처 묶음)
 * @param onClick	단추를 눌렀을 때 실행할 서브루틴
 */
class Button(x: () -> Float, y: () -> Float, width: Float, height: Float = 25f, caption: String, accessKey: Char? = null, private val color: Color = Color.WHITE, private val skin: Skin = defaultSkin, private val onClick: () -> Unit = {}) : Widget(x, y, width, height) {
	companion object {
		@JvmStatic val defaultSkin = Skin(HelperTextures.button, HelperTextures.buttonHover, HelperTextures.buttonPressed, HelperTextures.buttonDisabled, Color.WHITE, Color.LIGHT_GRAY);
	}

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
		val fontColor = if(!isEnabled) skin.disabledCaptionColor else skin.captionColor;

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
	private inline fun detectAccessKeyPress() {
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

	/**
	 * 버튼의 스킨이다.
	 *
	 * @param normal				기본 상태에서의 9-patch 텍스처
	 * @param hover					마우스를 올렸을 때의 9-patch 텍스처
	 * @param pressed				누르고 있는 동안의 9-patch 텍스처
	 * @param disabled				비활성화된 단추의 9-patch 텍스처
	 * @param captionColor			단추 글자 색
	 * @param disabledCaptionColor	비활성화된 단추 글자 색
	 */
	data class Skin(@JvmField val normal: NinePatch, @JvmField val hover: NinePatch = normal, @JvmField val pressed: NinePatch = normal, @JvmField val disabled: NinePatch = normal, @JvmField val captionColor: Color = Color.BLACK, @JvmField val disabledCaptionColor: Color = Color.GRAY);
}
