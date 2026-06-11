package io.potatogun.gdxhelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * 유용한 함수 모음
 */
object Utils {
	/**
	 * 초를 X분 X초로 변환한다
	 *
	 * @param seconds	초
	 */
	@JvmStatic
	@JvmOverloads
	fun parseSeconds(seconds: Int, minutesSuffix: String = " minute(s)", secondsSuffix: String = " second(s)"): String {
		if(seconds < 60) return "${seconds}$secondsSuffix";
		return "${seconds / 60}$minutesSuffix ${seconds % 60}$secondsSuffix";
	}

	/**
	 * R, G, B 값을 받아 Color 객체로 변환한다.
	 *
	 * @param	r	빨강 (0~255)
	 * @param	g	초록 (0~255)
	 * @param	b	파랑 (0~255)
	 * @param	a	알파 (0.0~1.0)
	 * @return	변환된 색 객체
	 */
	@JvmStatic
	@JvmOverloads
	inline fun rgb(r: Int, g: Int, b: Int, a: Float = 1.0f): Color {
		if(r > 255 || g > 255 || b > 255 || r < 0 || g < 0 || b < 0 || a > 1f || a < 0f)
			throw IllegalArgumentException("invalid color value");
		return Color(r / 255f, g / 255f, b / 255f, a);
	}

	/**
	 * 지정한 시간 후 특정 서브루틴을 한 번만 실행한다.
	 *
	 * 우리가 만든 Timer 클래스는 우리 게임의 상태 등에 종속적이지만 이쪽은 게임과는 독립적이기 때문에 libGDX의 Timer 클래스를 직접 사용한다.
	 * 
	 * @param delay		지연 시간(초)
	 * @param operation	실행할 서브루틴
	 */
	@JvmStatic inline fun setTimeout(delay: Float, crossinline operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				operation();
			}
		}.apply { Timer.schedule(this, delay) };
	}

	/**
	 * setTimeout으로 생성한 일회용 타이머를 취소한다.
	 */
	@JvmStatic inline fun clearTimeout(timeout: Task) {
		timeout.cancel();
	}

	/**
	 * 지정한 시간마다 특정 서브루틴을 실행한다.
	 *
	 * 우리가 만든 Timer 클래스는 우리 게임의 상태 등에 종속적이지만 이쪽은 게임과는 독립적이기 때문에 libGDX의 Timer 클래스를 직접 사용한다.
	 * 
	 * @param interval	실행 간격(초)
	 * @param operation	실행할 서브루틴
	 */
	@JvmStatic inline fun setInterval(interval: Float, crossinline operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				operation();
			}
		}.apply { Timer.schedule(this, interval, interval) };
	}

	/**
	 * setInterval으로 생성한 타이머를 취소한다.
	 */
	@JvmStatic inline fun clearInterval(interval: Task) {
		interval.cancel();
	}
	
	/**
     * 텍스트 그리기.
     *
     * 주의: y 축은 위쪽이 크다. '위쪽'에 글자를 쓰려면 y = screenHeight-10 처럼.
	 *
	 * @param batch				그리기 도구
	 * @param fpnt				글꼴
	 * @param text				출력할 메시지
	 * @param x					X 위치
	 * @param y					Y 위치
	 * @param color				글자 색
	 * @param scale				글자 크기(배)
	 * @param width				텍스트 상자의 크기 (오른쪽이나 가운데 정렬 시 반드시 필요)
	 * @param align				글자 정렬(없으면 왼쪽 정렬)
	 * @param skipBatch			batch.begin()/end() 사이에서 사용할 경우 true
     */
	@JvmStatic
	@JvmOverloads
	fun drawText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float? = null, align: Int = Align.left, skipBatch: Boolean = false) {
        font.color = color;
        font.data.setScale(scale);
        if(!skipBatch) batch.begin();
		val boxWidth: Float? = width;
		if(boxWidth != null)
			font.draw(batch, text, x, y, boxWidth, align, false);
		else
			font.draw(batch, text, x, y);
        if(!skipBatch) batch.end();
    }

	@JvmStatic inline fun max(x: Float, y: Float) = if(x > y) x else y;

	@JvmStatic inline fun abs(n: Float) = if(n < 0) -n else n;
}
