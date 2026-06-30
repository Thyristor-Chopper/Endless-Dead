package io.potatogun.gdxhelper.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import io.potatogun.gdxhelper.HelperTextures;
import io.potatogun.gdxhelper.Input;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;

import java.util.function.Supplier;

/**
 * 단추 - 코틀린용 생성자이며 자바 개발자라면 빌더를 사용하면 된다.
 *
 * x과 y 위치는 screenWidth 등이 포함될 경우 창 크기가 바뀔 때마다 값이 달라지므로 람다로 받는다.
 *
 * @constructor 동적 위치를 사용하는 생성자
 * @param    x       X 좌표 계산 함수
 * @param    y       Y 좌표 계산 함수
 * @param    width   단추 너비 계산 함수
 * @param    height  단추 높이 계산 함수
 * @param    caption 단추 라벨
 * @property skin    단추의 스킨(텍스처 묶음)
 * @property color   단추의 색
 * @property onClick 단추를 눌렀을 때 실행할 서브루틴
 */
class Button(x: () -> Float, y: () -> Float, width: () -> Float, height: () -> Float = { 25f }, caption: String, private val skin: Skin = defaultSkin, private val color: Color = Color.WHITE, private val onClick: () -> Unit = {}) : Widget(x, y, width, height) {
	companion object {
		/**
		 * 프레임워크에서 제공하는 단추의 기본 스킨
		 */
		private val defaultSkin = Skin(HelperTextures.button, HelperTextures.buttonHover, HelperTextures.buttonPressed, HelperTextures.buttonDisabled, Color.WHITE, Color.LIGHT_GRAY);
	}

	private val font = BitmapFont();
	private val accessKey: Char?;
	private val caption: String;
	private var previouslyPressed = false;
	private var isEnabled = true;

	/**
	 * 단추 - 코틀린용 생성자이며 자바 개발자라면 빌더를 사용하면 된다.
	 *
	 * @constructor 정적 위치를 사용하는 생성자
	 * @param x       X 좌표
	 * @param y       Y 좌표
	 * @param width   단추 너비
	 * @param height  단추 높이
	 * @param caption 단추 라벨
	 * @param skin    단추의 스킨(텍스처 묶음)
	 * @param color   단추의 색
	 * @param onClick 단추를 눌렀을 때 실행할 서브루틴
	 */
	constructor(x: Float, y: Float, width: Float, height: Float = 25f, caption: String, skin: Skin = defaultSkin, color: Color = Color.WHITE, onClick: () -> Unit = {}) : this({ x }, { y }, { width }, { height }, caption, skin, color, onClick);

	init {
		val accessKeyMatch = Regex("[&]([A-Za-z])");
		this.accessKey = accessKeyMatch.find(caption)?.value?.get(1)?.uppercaseChar();
		this.caption = caption.replaceFirst(accessKeyMatch, "$1");
	}

	override fun draw(batch: SpriteBatch) {
		val x = this.x();
		val y = this.y();
		val width = this.width();
		val height = this.height();

		val mouseX = Input.mouseX.toFloat();
		val mouseY = Window.height - Input.mouseY;
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
		Utils.drawText(batch, font, caption, x, y + height * 0.5f + 6f, fontColor, 1.0f, width, Align.center);

		detectAccessKeyPress();
	}

	/**
	 * 현재 프레임에서 바로 가기 키 처리
	 */
	private fun detectAccessKeyPress() {
		if(accessKey != null && Input.isKeyJustPressed(accessKey.code - 36))
			onClick();
	}

	/**
	 * 단추를 누를 수 있게 한다.
	 */
	fun enable() {
		isEnabled = true;
	}

	/**
	 * 단추를 누를 수 없게 한다.
	 */
	fun disable() {
		isEnabled = false;
	}

	/**
	 * 단추 누르기
	 */
	private fun fireClickEvent() {
		if(!previouslyPressed) return;
		onClick();
		previouslyPressed = false;
	}

	/**
	 * 버튼의 스킨이다.
	 *
	 * @property normal               기본 상태에서의 9-patch 텍스처
	 * @property hover                마우스를 올렸을 때의 9-patch 텍스처
	 * @property pressed              누르고 있는 동안의 9-patch 텍스처
	 * @property disabled             비활성화된 단추의 9-patch 텍스처
	 * @property captionColor         단추 글자 색
	 * @property disabledCaptionColor 비활성화된 단추 글자 색
	 */
	data class Skin(@JvmField val normal: NinePatch, @JvmField val hover: NinePatch = normal, @JvmField val pressed: NinePatch = normal, @JvmField val disabled: NinePatch = normal, @JvmField val captionColor: Color = Color.BLACK, @JvmField val disabledCaptionColor: Color = Color.GRAY);

	/**
	 * 단추 빌더 (자바 개발자 전용)
	 *
	 * @constructor 동적 크기를 사용하는 위젯
	 * @param x      X 좌표 계산 함수
	 * @param y      Y 좌표 계산 함수
	 * @param width  단추 너비 계산 함수
	 * @param height 단추 높이 계산 함수
	 */
	class Builder(private val x: Supplier<Float>, private val y: Supplier<Float>, private val width: Supplier<Float>, private val height: Supplier<Float>) {
		private var caption = "";
		private var skin = defaultSkin;
		private var color = Color.WHITE;
		private lateinit var clickHandler: () -> Unit;

		/**
		 * 단추 빌더 (자바 개발자 전용)
		 *
		 * @constructor 정적 크기를 사용하는 위젯
		 * @param x      X 좌표
		 * @param y      Y 좌표
		 * @param width  단추 너비
		 * @param height 단추 높이
		 */
		constructor(x: Float, y: Float, width: Float, height: Float) : this({ x }, { y }, { width }, { height });

		fun caption(caption: String): Builder {
			this.caption = caption;
			return this;
		}

		fun skin(skin: Skin): Builder {
			this.skin = skin;
			return this;
		}

		fun color(color: Color): Builder {
			this.color = color;
			return this;
		}

		fun onClick(handler: Runnable): Builder {
			this.clickHandler = { handler.run() };
			return this;
		}

		fun build(): Button {
			if(!::clickHandler.isInitialized)
				throw IllegalStateException("click handler is not set");
			return Button({ x.get() }, { y.get() }, { width.get() }, { height.get() }, caption, skin, color, clickHandler);
		}
	}
}
