package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.Sort;

/**
 * 유용한 함수 모음
 */
object Utils {
	/**
	 * libGDX에서 제공하는 정렬기이다.
	 */
	@JvmField val sorter = Sort.instance();

	/**
	 * 초를 X' XX''로 변환한다.
	 *
	 * @param seconds 초
	 * @return        변환된 문자열
	 */
	@JvmStatic inline fun parseSeconds(seconds: Int): String = parseSeconds(seconds, "'", "\"");

	/**
	 * 초를 X분 X초로 변환한다.
	 *
	 * @param seconds       초
	 * @param minutesSuffix 분 표기
	 * @param secondsSuffix 초 표기
	 * @return              변환된 문자열
	 */
	@JvmStatic fun parseSeconds(seconds: Int, minutesSuffix: String, secondsSuffix: String): String {
		if(seconds < 60) return "${seconds}$secondsSuffix";
		return "${seconds / 60}$minutesSuffix ${seconds % 60}$secondsSuffix";
	}

	/**
	 * R, G, B 값을 받아 Color 객체로 변환한다.
	 *
	 * @param r 빨강 (0~255)
	 * @param g 초록 (0~255)
	 * @param b 파랑 (0~255)
	 * @param a 알파 (0.0~1.0)
	 * @return	변환된 색 객체
	 * @throws IllegalArgumentException 색 값이 잘못된 경우
	 */
	@JvmStatic @JvmOverloads inline fun rgb(r: Int, g: Int, b: Int, a: Float = 1.0f): Color {
		if(r > 255 || g > 255 || b > 255 || r < 0 || g < 0 || b < 0 || a > 1f || a < 0f)
			throw IllegalArgumentException("invalid color value");
		return Color(r / 255f, g / 255f, b / 255f, a);
	}

	/**
	 * 텍스트 그리기
	 *
	 * 주의: y 축은 위쪽이 크다. '위쪽'에 글자를 쓰려면 y = screenHeight-10 처럼.
	 *
	 * @param batch 그리기 도구
	 * @param font  글꼴
	 * @param text  출력할 메시지
	 * @param x     X 위치
	 * @param y     Y 위치
	 * @param color 글자 색
	 * @param scale 글자 크기(배)
	 * @param width 텍스트 상자의 크기, 0은 자동 (오른쪽이나 가운데 정렬 시 반드시 필요, 이때는 0 불가)
	 * @param align 글자 정렬(없으면 왼쪽 정렬)
	 */
	@JvmStatic @JvmOverloads fun drawText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float = 0f, align: Int = Align.left) {
		val skipBatch = batch.isDrawing();
		font.color = color;
		font.data.setScale(scale);
		if(!skipBatch) batch.begin();
		if(width != 0f)
			font.draw(batch, text, x, y, width, align, false);
		else
			font.draw(batch, text, x, y);
		if(!skipBatch) batch.end();
	}

	/**
	 * 지정한 libGDX 배열을 지정한 비교기를 이용하여 정렬한다.
	 *
	 * @param array      정렬할 배열
	 * @param comparator 비교기
	 */
	@JvmStatic inline fun <T> sortWith(array: GdxArray<T>, comparator: Comparator<T>) {
		sorter.sort(array, comparator);
	}
}
