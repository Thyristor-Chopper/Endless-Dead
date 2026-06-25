package io.potatogun.gdxhelper.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.util.MutablePosition;
import io.potatogun.gdxhelper.util.Position;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math;

import kotlin.math.atan2;

/**
 * 게임에 등장하는 모든 '무엇인가'의 공통 부모.
 *
 * ────────────────────────────────────────────────────────────
 *  왜 이런 게 필요한가?
 * ────────────────────────────────────────────────────────────
 *  Player, Enemy, Bullet은 기능은 다르지만
 *    - 화면의 특정 위치(x, y)에 있고
 *    - 어떤 크기(width, height)를 가지고
 *    - 매 프레임 스스로 상태를 갱신(update)하고
 *    - 자신을 그릴 줄(draw) 안다
 *  이 '공통 속성/행동'을 한 곳에 모아둔 것이 Entity이다.
 *
 *  World는 이 Entity 타입으로만 개체들을 관리한다.
 *  즉, 우리가 Player든 Bullet이든 'Entity를 상속'하기만 하면
 *  World가 자동으로 update/draw/제거까지 해준다 (다형성).
 *
 * ────────────────────────────────────────────────────────────
 *  사용법 — 새로운 게임 개체 만들기
 * ────────────────────────────────────────────────────────────
 *    class Bullet(world: World, x: Float, y: Float) : Entity(world, x, y, 8f, 16f, "bullet.png") {
 *        override fun update(delta: Float) { y += 400f * delta }
 *    }
 *
 * @property world   개체가 속한 세계 - 자바로 만든 게임에서도 이런 때는 getWorld()를 쓰는 경우가 많아 @JvmField는 안 붙임
 * @param    x       개체의 처음 X 위치
 * @param    y       개체의 처음 Y 위치
 * @property width   가로 크기 (픽셀)
 * @property height  세로 크기 (픽셀)
 * @property texture 개체 텍스처(없을 수도 있음) - @JvmField가 있지만 protected라 외부 자바 클래스에서 접근하라고 있는 게 아니기 때문에 캠슐화가 많이 깨지지는 않는 것 같아 성능을 위해서 씀.
 */
abstract class Entity(val world: World, x: Float, y: Float, @JvmField val width: Float, @JvmField val height: Float, @JvmField protected val texture: Texture? = null) {
	/**
	 * 개체의 평면좌표 위치
	 *
	 * @JvmField는 성능 때문도 있지만, 외부 자바 클래스에서 이 클래스를 접근한다고 생각해보면
	 *   이게 없으면 entity.getPosition()이 될텐데 위치를 바꾼다고 생각해보자.
	 *   그럼 entity.getPosition().setX(3);같이 될텐데 'get'을 해 놓고 set을 하는 게 좀 어색하지 않을까.
	 *   어차피 val(final)이고 클래스 생성 시 바로 Position 객체가 할당되니까 null 위험성도 없다.
	 */
	@JvmField val position = MutablePosition(x, y).apply {
		setObserver { _, _ ->
			isCachedRectValid = false;
			world.entities.updateTile(this@Entity);
		};
	};
	// x과 y를 필드로 바로 노출 (내부적으로 position과 상호작용)
	//   기존에는 x과 y가 backing field가 있는 실제 var였고 
	//   val position get() = Position(x, y)가 있었다.
	//   하지만 매번 Position 객체를 새로 생성하는 것은 오버헤드가 상당할 것 같아서
	//   이렇게 바꾸었다.
	/**
	 * 개체의 현재 X 좌표
	 */
	var x: Float
		inline get() = position.x
		inline set(value) { position.x = value };
	/**
	 * 개체의 현재 Y 좌표
	 */
	var y: Float
		inline get() = position.y
		inline set(value) { position.y = value };
	/**
	 * Freezable 월드에서 시간 정지 영향을 받는지의 여부
	 */
	open val isUpdatableWhileFrozen = false;
	/**
	 * 텍스처 회전 각도
	 */
	private var rotation = 0f;
	/**
	 * 개체 오버레이 색
	 *   (color는 mutable 객체이므로 val)
	 */
	protected open val color = Color.WHITE;
	/**
	 * 개체의 투명도
	 */
	protected open var opacity: Float
		get() = color.a
		set(value) {
			if(value < 0f) color.a = 0f;
			else if(value > 1f) color.a = 1f;
			else color.a = value;
		};
	/**
	 * 충돌 감지용 너비 (캐시)
	 */
	private var collideCheckWidth = width;
	/**
	 * 충돌 감지용 높이 (캐시)
	 */
	private var collideCheckHeight = height;
	/**
	 * 개체가 차지하는 사각형 영역 (캐시)
	 */
	private var cachedRect = calculateRect();
	/**
	 * 개체가 차지하는 사각형 영역의 유효 여부 (캐시용)
	 */
	private var isCachedRectValid = true;

	/**
	 * 매 프레임 호출되어 자신을 그린다.
	 *
	 * 하위 클래스는 이 함수를 override하여 상황에 따라 텍스처를 달리하여 super.draw(SpriteBatch, Texture)를 호출할 수 있다.
	 * 
	 * @param batch 이미지(Texture)를 화면에 찍어주는 도구. GameWorld가 이미 projectionMatrix 를 세팅하고 begin()/end() 안에서 호출해주므로, 서브클래스는 batch.draw(texture, x, y, w, h) 한 줄만 적으면 된다.
	 */
	open fun draw(batch: SpriteBatch) {
		draw(batch, null);
	}

	/**
	 * 매 프레임 호출되어 자신을 그린다.
	 *   자식 클래스에서 draw(SpriteBatch)를 override하여 상황에 맞는 텍스처를 지정하여
	 *   개체에 등록된 기본 텍스처 대신에 쓸 텍스처를 alternateTexture로 넘길 수 있다.
	 *
	 * @param batch            이미지(Texture)를 화면에 찍어주는 도구
	 * @param alternateTexture 대신 사용할 텍스처
	 */
	protected open fun draw(batch: SpriteBatch, alternateTexture: Texture?) {
		val texture: Texture? = alternateTexture ?: this.texture;
		texture?.let {
			if(batch.color == Color.WHITE) batch.color = color;  // 대미지 시 붉게가 작동하게 하기 위해.

			val halfWidth = width * 0.5f;
			val halfHeight = height * 0.5f;
			batch.draw(it, x - halfWidth, y - halfHeight, halfWidth, halfHeight, width, height, 1.0f, 1.0f, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
			batch.color = Color.WHITE;
		};
	}

	/**
	 * 개체가 차지하는 사각형 영역을 새로 계산한다.
	 *
	 * @return 사각형
	 */
	private inline fun calculateRect(): Rectangle = Rectangle(x - collideCheckWidth * 0.5f, y - collideCheckHeight * 0.5f, collideCheckWidth, collideCheckHeight);

	/**
	 * 이 객체가 차지하는 사각형 영역
	 *
	 * @return 사각형
	 */
	fun getBounds(): Rectangle {
		if(!isCachedRectValid) {
			cachedRect = calculateRect();
			isCachedRectValid = true;
		}
		return cachedRect;
	}

	/**
	 * 다른 객체와 충돌했는지 검사 — AABB(축 정렬 경계 상자) 방식.
	 *
	 * 두 사각형이 한 픽셀이라도 겹치면 true.
	 *   더 정밀한 판정(원, 다각형, 픽셀 단위)이 필요하면 서브클래스에서
	 *   별도 메서드를 만들거나 이 메서드를 override 해서 바꿀 수 있다.
	 *
	 * 왜 Entity에 둘까?
	 *   모든 게임 객체가 '충돌할 수 있다' 는 공통 능력을 가지기 때문.
	 *   그래서 player.collidesWith(enemy), bullet.collidesWith(wall) 처럼
	 *   어떤 조합이든 똑같은 문법으로 쓸 수 있다.
	 *
	 * @param other 비교 대상
	 * @return      충돌하면 true
	 */
	fun collidesWith(other: Entity): Boolean {
		// 원본 코드: getBounds().overlaps(other.getBounds()); 한 줄
		//   좀비처럼 게속 움직이는 개체는 매번 새 사각형 객체를 만들어서 확인하여 오버헤드도 상당하고
		//   update() 내에서 collidesWith하는 경우도 많아 GC할 거리도 매 프레임 엄청나게 불어난다.

		return Utils.abs(x - other.x) < (collideCheckWidth + other.collideCheckWidth) * 0.5f && Utils.abs(y - other.y) < (collideCheckHeight + other.collideCheckHeight) * 0.5f;
	}

	/**
	 * 개체가 다른 누군가를 공격했을 때 콜백 함수
	 * 확장성을 고려하여 LivingEntity가 아닌 그냥 Entity이다. 현재로썬 큰 의미는 없지만.
	 *
	 * @param victim 공격 대상
	 */
	open fun onAttack(victim: Entity) {}

	/**
	 * 개체가 다른 누군가를 처치했을 때 콜백 함수
	 * 확장성을 고려하여 LivingEntity가 아닌 그냥 Entity이다. 현재로썬 큰 의미는 없지만.
	 *
	 * @param victim 공격 대상
	 */
	open fun onKill(victim: Entity) {}

	/**
	 * 다른 개체와의 거리 (몸의 중앙을 기준으로 한다)
	 *
	 * @param other 대상 개체
	 * @return      떨어진 거리
	 */
	fun distanceTo(other: Entity): Float = position.distanceTo(other.position);

	/**
	 * 회전 각도를 구한다.
	 *
	 * @return 회전 각도
	 */
	fun getRotationAngle(): Float = rotation;

	/**
	 * 특정 위치를 향해 회전한다.
	 * 
	 * @param position 타겟 방향
	 */
	fun rotateTo(position: Position) {
		// 샷건 내 360도 구현 참고함
		rotate(Math.toDegrees(atan2((Window.height - position.y) - (this.y - world.offsetY + Window.height * 0.5f), position.x - (this.x - world.offsetX + Window.width * 0.5f)).toDouble()).toFloat() - 90f);
	}

	/**
	 * 개체를 회전한다.
	 * 
	 * @param degrees 회전 각도
	 */
	fun rotate(degrees: Float) {
		if(rotation == degrees) return;

		rotation = degrees;

		// 충돌 판정 dimension 다시 계산
		if(rotation % 180f == 0f) {
			collideCheckWidth = width;
			collideCheckHeight = height;
		} else if((rotation + 90f) % 180f == 0f) {
			collideCheckWidth = height;
			collideCheckHeight = width;
		} else {  // 사각형의 세밀한 회전을 구하기에는 연산량이 너무 늘어나니까 그냥 평균으로 때우자. 어차피 지금 플레이어 외에 회전하는 개체가 없다.
			collideCheckWidth = (width + height) * 0.5f;
			collideCheckHeight = collideCheckWidth;
		}

		isCachedRectValid = false;
	}

	/**
	 * 매 프레임 호출되어 상태를 갱신한다.
	 *
	 * 상자나 물체처럼 로직이 없는 개체일 수도 있으니 기본은 빈 함수.
	 *
	 * @param delta 직전 프레임과의 시간 간격(초). 60fps 면 약 0.0167.
	 *              '픽셀/초' 단위의 속도에 delta 를 곱하면 '이번 프레임 이동량' 이 된다.
	 *              (프레임 속도가 달라져도 같은 속도로 움직이게 하려는 공식)
	 */
	open fun update(delta: Float) {}

	/**
	 * 시간이 멈췄어도 isUpdatableWhileFrozen에 관계없이 실행할 로직
	 *
	 * @param delta 직전 프레임과의 시간 간격(초)
	 */
	open fun forceUpdate(delta: Float) {}

	/**
	 * 개체를 월드에서 제거하고 등록을 해제한다.
	 */
	inline fun remove() {
		world.entities.remove(this);
	}

	/**
	 * 이 객체가 갖고 있는 GPU 자원을 정리한다 — 화면이 닫힐 때 한 번 호출된다.
	 *
	 * 왜 필요한가?
	 *   Texture, Sound 같은 LibGDX 자원은 GPU/네이티브 메모리를 점유한다.
	 *   garbage collector는 이 메모리를 해제해 주지 못한다 - dispose() 명시적 호출 필요.
	 */
	open fun dispose() {
		texture?.dispose();
	}
}
