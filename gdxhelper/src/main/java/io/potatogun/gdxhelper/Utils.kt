package io.potatogun.gdxhelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import io.potatogun.gdxhelper.util.SharedTextureManager;

/**
 * 유용한 함수 모음
 */
object Utils {
	private val sorter = Sort.instance();

	// 수동 오버로딩
	@JvmStatic inline fun parseSeconds(seconds: Int): String = parseSeconds(seconds, " minute(s)", " second(s)");

	/**
	 * 초를 X분 X초로 변환한다
	 *
	 * @param seconds 초
	 * @return        변환된 문자열
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
	 * @param delay     지연 시간(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmStatic fun setTimeout(delay: Float, condition: (() -> Boolean)? = null, operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				if(condition?.invoke() ?: true)
					operation();
			}
		}.also { Timer.schedule(it, delay) };
	}

	/**
	 * setTimeout으로 생성한 일회용 타이머를 취소한다.
	 *
	 * @param timeout 취소할 작업 객체
	 */
	@JvmStatic inline fun clearTimeout(timeout: Task) {
		timeout.cancel();
	}

	/**
	 * 지정한 시간마다 특정 서브루틴을 실행한다.
	 *
	 * 우리가 만든 Timer 클래스는 우리 게임의 상태 등에 종속적이지만 이쪽은 게임과는 독립적이기 때문에 libGDX의 Timer 클래스를 직접 사용한다.
	 * 
	 * @param interval  실행 간격(초)
	 * @param condition 실행 조건
	 * @param operation 실행할 서브루틴
	 * @return          실행 작업 객체
	 */
	@JvmStatic fun setInterval(interval: Float, condition: (() -> Boolean)? = null, operation: () -> Unit): Task {
		return object : Task() {
			override fun run() {
				if(condition?.invoke() ?: true)
					operation();
			}
		}.also { Timer.schedule(it, interval, interval) };
	}

	/**
	 * setInterval으로 생성한 타이머를 취소한다.
	 *
	 * @param timeout 취소할 작업 객체
	 */
	@JvmStatic inline fun clearInterval(interval: Task) {
		interval.cancel();
	}

	/**
	 * 텍스트 그리기.
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
	@JvmStatic
	@JvmOverloads
	fun drawText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f, width: Float = 0f, align: Int = Align.left) {
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
	 * 두 수 중 최댓값을 반환한다.
	 *
	 * @param x 첫째 수
	 * @param y 둘째 수
	 * @return  최댓값
	 */
	@JvmStatic inline fun max2(x: Float, y: Float): Float = if(x > y) x else y;

	/**
	 * 지정한 수의 절댓값을 반환한다.
	 *
	 * @param n 처리할 수
	 * @return  절댓값
	 */
	@JvmStatic inline fun abs(n: Float): Float = if(n < 0) -n else n;

	/**
	 * 지정한 화일 이름의 텍스처를 가져온다.
	 *
	 * @param path 화일 이름
	 * @return     불러온 텍스처 객체
	 */
	@JvmStatic inline fun loadTexture(path: String): Texture = Texture(Gdx.files.internal("assets/textures/$path"));

	/**
	 * 공유 자원이 아닌 경우에만 비디오 카드 메모리에서 정리한다.
	 *
	 * @param texture 해제할 텍스처
	 * @return        성공 여부
	 */
	@JvmStatic inline fun safeDispose(texture: Texture): Boolean = texture.takeIf { !SharedTextureManager.isSharedTexture(it) }?.let { it.dispose(); true } ?: false;

	@JvmStatic fun <T> sortWith(array: GdxArray<T>, comparator: Comparator<T>) {
		sorter.sort(array, comparator);
	}
}
