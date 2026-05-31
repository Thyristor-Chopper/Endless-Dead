package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import io.potatogun.endlessdead.Utils;
import io.potatogun.endlessdead.entity.Entity;
import io.potatogun.endlessdead.position.MutablePosition;
import io.potatogun.endlessdead.position.Position;
import io.potatogun.endlessdead.position.toMutablePosition;
import io.potatogun.endlessdead.world.World;

import java.lang.Math.toDegrees;

import kotlin.math.atan2;
import kotlin.math.sqrt;

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
 * @param world		개체가 속한 세계
 * @param position	개체의 처음 위치
 * @param width		가로 크기 (픽셀)
 * @param height	세로 크기 (픽셀)
 * @param texture	개체 텍스처(없을 수도 있음)
 */
abstract class Entity(val world: World, position: Position, @JvmField val width: Float, @JvmField val height: Float, @JvmField protected val texture: Texture? = null) {
	/**
	 * 개체의 평면좌표 위치
	 */
	@JvmField val position = position.toMutablePosition();
	// x과 y를 필드로 바로 노출 (내부적으로 position과 상호작용)
	//   기존에는 x과 y가 backing field가 있는 실제 var였고 
	//   val position / get() = Position(x, y)가 있었다.
	//   하지만 매번 Position 객체를 새로 생성하는 것은 오버헤드가 상당할 것 같아서
	//   이렇게 바꾸었다.
	var x: Float
		inline get() = position.x
		inline set(value) { position.x = value };
	var y: Float
		inline get() = position.y
		inline set(value) { position.y = value };
	/**
	 * TimeStopper 아이템의 영향을 받는지의 여부
	 */
	open val canUpdateWhileFrozen = false;
	/**
	 * 다른 개체에 닿았을 때 몸 대미지(아직 활용하는 개체 없음)
	 */
	open val bodyDamage = 0;
	// 동일 개체에 대해 몸 대미지 무시 여부
	protected open val ignoreFriendBodyDamage = false;
	/**
	 * 총알이 관통할 때 총알에게 주는 대미지
	 */
	open val penetrationDamage = 0;
	// 텍스처 회전 각도
	open var rotation = 0f
		protected set;
	// 개체 오버레이 색 (color는 mutable 객체이므로 val)
	protected open val color = Color.WHITE;
	// 개체 투명도
	protected open var opacity: Float
		get() = color.a
		set(value) {
			if(value < 0f || value > 1f)
				throw IllegalArgumentException("invalid opacity");
			color.a = value;
		};

	/**
     * 매 프레임 호출되어 자신을 그린다.
	 *
	 * 하위 클래스는 이 함수를 override하여 상황에 따라 텍스처를 달리하여 super.draw(SpriteBatch, Texture)를 호출할 수 있다.
	 * 
     * @param batch 이미지(Texture)를 화면에 찍어주는 도구. GameWorld가 이미 projectionMatrix 를 세팅하고 begin()/end() 안에서 호출해주므로, 서브클래스는 batch.draw(texture, x, y, w, h) 한 줄만 적으면 된다.
	 */
	internal open fun draw(batch: SpriteBatch) {
		draw(batch, null);
	}

    // 개체에 등록된 기본 텍스처 대신에 쓸 텍스처를 alternateTexture로 넘길 수 있다.
    protected open fun draw(batch: SpriteBatch, alternateTexture: Texture?) {
		val texture: Texture? = alternateTexture ?: this.texture;
		texture?.let {
			if(batch.color == Color.WHITE) batch.color = color;  // 대미지 시 붉게가 작동하게 하기 위해.
			batch.draw(it, x - width / 2f, y - height / 2f, width / 2f, height / 2f, width, height, 1.0f, 1.0f, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
			batch.color = Color.WHITE;
		};
	}

    /**
     * 이 객체가 차지하는 사각형 영역 — 충돌 판정에 쓴다.
     *
     * 매번 새 Rectangle 을 만든다. 성능이 극한으로 중요한 곳이라면 재사용해야
     * 하지만, 이 강의의 규모에서는 가독성을 더 우선한다.
     */
    inline fun getBounds(): Rectangle = Rectangle(x - width / 2f, y - height / 2f, width, height);

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
     */
    inline fun collidesWith(other: Entity): Boolean = getBounds().overlaps(other.getBounds());

	/**
	 * 개체가 다른 누군가를 공격했을 때 콜백 함수
	 *
	 * @param victim	공격 대상
	 */
	open fun onAttack(victim: LivingEntity) {}

	/**
	 * 개체가 다른 누군가를 처치했을 때 콜백 함수
	 *
	 * @param victim	공격 대상
	 */
	open fun onKill(victim: LivingEntity) {}

	/**
	 * 다른 개체와의 거리 (몸의 중앙을 기준으로 한다)
	 *
	 * @param	other	대상 개체
	 * @return	떨어진 거리
	 */
	fun distanceTo(other: Entity): Float = position.distanceTo(other.position);

	/**
	 * 특정 위치를 향해 회전한다.
	 * 
	 * @param position 타겟 방향
	 */
	fun rotateTo(position: Position) {
		// 샷건 내 360도 구현 참고함
		rotation = toDegrees(atan2((world.game.screenHeight - position.y) - (this.y - world.offsetY + world.game.screenHeight / 2f), position.x - (this.x - world.offsetX + world.game.screenWidth / 2f)).toDouble()).toFloat() - 90f;
	}

	/**
	 * 개체를 회전한다.
	 * 
	 * @param degrees 회전 각도
	 */
	fun rotate(degrees: Float) {
		rotation = degrees;
	}

	/**
     * 매 프레임 호출되어 **상태를 갱신**한다.
     *
     * 상자나 물체처럼 로직이 없는 개체일 수도 있으니 기본은 빈 함수
     *
     * @param delta 직전 프레임과의 시간 간격(초). 60fps 면 약 0.0167.
     *              '픽셀/초' 단위의 속도에 delta 를 곱하면 '이번 프레임 이동량' 이 된다.
     *              (프레임 속도가 달라져도 같은 속도로 움직이게 하려는 공식)
     */
    internal open fun update(delta: Float) {}

	/**
	 * 시간이 멈췄어도 canUpdateWhileFrozen에 관계없이 실행할 로직
	 */
	internal open fun forceUpdate(delta: Float) {}

    /**
     * 이 객체가 갖고 있는 GPU 자원을 정리한다 — 화면이 닫힐 때 한 번 호출된다.
     *
     * 왜 필요한가?
     *   Texture, Sound 같은 LibGDX 자원은 GPU/네이티브 메모리를 점유한다.
     *   garbage collector는 이 메모리를 해제해 주지 못한다 → dispose() 명시적 호출 필요.
     *
     * 기본 구현은 빈 함수 — Texture 같은 자원을 안 쓰는 객체는 그대로 두면 된다.
     * 텍스처를 쓰는 객체라면 override 해서 texture.dispose()를 호출.
     */
    internal open fun dispose() {
		texture?.dispose();
	}
}
