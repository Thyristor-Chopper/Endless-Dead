package com.oop.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import com.oop.game.GameObject;
import com.oop.game.Position;
import com.oop.game.Updatable;
import com.oop.game.WorldObject;
import com.oop.game.entity.Entity;
import com.oop.game.world.World;

import kotlin.math.sqrt;

/**
 * 게임에 등장하는 모든 '무엇인가'의 공통 부모.
 *
 * ────────────────────────────────────────────────────────────
 *  왜 이런 게 필요한가?
 * ────────────────────────────────────────────────────────────
 *  Player, Enemy, Bullet 은 기능은 다르지만
 *    - 화면의 특정 위치(x, y)에 있고
 *    - 어떤 크기(width, height)를 가지고
 *    - 매 프레임 스스로 상태를 갱신(update)하고
 *    - 자신을 그릴 줄(draw) 안다
 *  이 '공통 속성/행동'을 한 곳에 모아둔 것이 GameObject 이다.
 *
 *  GameWorld 는 이 GameObject 타입으로만 객체들을 관리한다.
 *  즉, 우리가 Player 든 Bullet 이든 **GameObject를 상속**하기만 하면
 *  GameWorld 가 자동으로 update/draw/제거까지 해준다 (다형성).
 *
 * ────────────────────────────────────────────────────────────
 *  사용법 — 새로운 게임 객체 만들기
 * ────────────────────────────────────────────────────────────
 *    class Bullet(x: Float, y: Float) : GameObject(x, y, 8f, 16f) {
 *        private val texture = Texture(Gdx.files.internal("bullet.png"))
 *        override fun update(delta: Float) { y += 400f * delta }
 *        override fun draw(batch: SpriteBatch) {
 *            batch.draw(texture, x, y, width, height)
 *        }
 *        override fun dispose() { texture.dispose() }
 *    }
 *
 * @param world		개체가 속한 세계
 * @param x			왼쪽 아래 꼭짓점의 월드 좌표 x
 * @param y 		왼쪽 아래 꼭짓점의 월드 좌표 y
 * @param width		가로 크기 (픽셀)
 * @param height	세로 크기 (픽셀)
 * @param texture	아이템 텍스처(없을 수도 있음)
 */
abstract class Entity(override val world: World, var x: Float, var y: Float, val width: Float, val height: Float, texture: String? = null) : GameObject, WorldObject, Updatable {
	override val game = world.game;
	protected val texture: Texture? = texture?.let { Texture(Gdx.files.internal(it)) };
	private val textureWidth: Int? = this.texture?.getWidth();
	private val textureHeight: Int? = this.texture?.getHeight();
	val position: Position
		get() = Position(x, y);
	open val bodyDamage = 0;  // 다른 개체에 닿았을 때 몸 대미지(아직 활용하는 개체 없음)
	protected open val ignoreFriendBodyDamage = false;  // 동일 개체에 대해 몸 대미지 무시
	open val penetrationDamage = 0;  // 총알이 관통할 때 총알에게 주는 대미지
	protected open var rotation = 0f;

    /**
     * 매 프레임 호출되어 **자신을 그린다**.
     *
     * @param batch SpriteBatch — 이미지(Texture)를 화면에 찍어주는 도구.
     *              GameWorld 가 이미 projectionMatrix 를 세팅하고
     *              begin()/end() 안에서 호출해주므로, 서브클래스는
     *              batch.draw(texture, x, y, w, h) 한 줄만 적으면 된다.
     *
     * 이미지 로딩은 보통 객체의 init 또는 프로퍼티 초기화 시점에 한 번 한다:
     *   private val texture = Texture(Gdx.files.internal("player.png"))
     */
    open fun draw(batch: SpriteBatch) {
		texture?.let { batch.draw(it, x - width / 2f, y - height / 2f, width / 2f, height / 2f, width, height, 1.0f, 1.0f, rotation, 0, 0, textureWidth!!, textureHeight!!, false, false) };
	}

    /**
     * 이 객체가 차지하는 사각형 영역 — 충돌 판정에 쓴다.
     *
     * 매번 새 Rectangle 을 만든다. 성능이 극한으로 중요한 곳이라면 재사용해야
     * 하지만, 이 강의의 규모에서는 가독성을 더 우선한다.
     */
    inline fun getBounds(): Rectangle = Rectangle(x, y, width, height);

    /**
     * 다른 객체와 충돌했는지 검사 — AABB(축 정렬 경계 상자) 방식.
     *
     * 두 사각형이 한 픽셀이라도 겹치면 true.
     *   더 정밀한 판정(원, 다각형, 픽셀 단위)이 필요하면 서브클래스에서
     *   별도 메서드를 만들거나 이 메서드를 override 해서 바꿀 수 있다.
     *
     * 왜 GameObject 에 둘까?
     *   모든 게임 객체가 '충돌할 수 있다' 는 공통 능력을 가지기 때문.
     *   그래서 player.collidesWith(enemy), bullet.collidesWith(wall) 처럼
     *   어떤 조합이든 똑같은 문법으로 쓸 수 있다.
     */
    inline fun collidesWith(other: Entity): Boolean {
        return getBounds().overlaps(other.getBounds());
    }

    /**
     * 이 객체가 갖고 있는 GPU 자원을 정리한다 — 화면이 닫힐 때 한 번 호출된다.
     *
     * 왜 필요한가?
     *   Texture, Sound 같은 LibGDX 자원은 GPU/네이티브 메모리를 점유한다.
     *   garbage collector 는 이 메모리를 해제해 주지 못한다 → dispose() 명시적 호출 필요.
     *
     * 기본 구현은 빈 함수 — Texture 같은 자원을 안 쓰는 객체는 그대로 두면 된다.
     * 텍스처를 쓰는 객체라면 override 해서 texture.dispose() 를 호출.
     */
    open fun dispose() {
		texture?.dispose();
	}
	
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
	
	fun distanceTo(other: Entity): Float {
		val dx = (other.x + other.width / 2f - width / 2f) - x;
        val dy = (other.y + other.height / 2f - height / 2f) - y;
        return sqrt(dx * dx + dy * dy);
	}
}
