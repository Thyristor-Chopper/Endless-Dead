package io.potatogun.endlessdead.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer.Task;

import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.util.Position;
import io.potatogun.gdxhelper.world.World;

import java.lang.Math.toRadians;

import kotlin.math.cos;
import kotlin.math.sin;

/**
 * 총 아이템 추상 클래스
 *
 * @JvmField가 있는 곳은 빌드 후 직접 디컴파일하여 null이 불가능한 원시 int, float로 바뀜을 확인했다.
 *
 * @param world					아이템이 있는 세계
 * @param id					총 식별자
 * @param name					총 이름
 * @param bulletDamage			총알 피해량
 * @param bulletSpeed			총알 속도
 * @param bulletHP				총알 체력
 * @param isBulletPenetreble	총알 관통 가능 여부
 * @param fireInterval			공격 속도
 * @param initialBullets		초기 총알 개수
 * @param maxBullets			최대 총알 개수
 */
abstract class Gun(world: World, id: String, name: String, val bulletDamage: Int, val bulletSpeed: Float, @get:JvmName("getBulletHP") val bulletHP: Int, @JvmField val isBulletPenetreble: Boolean, val fireInterval: Float, initialBullets: Int, @JvmField val maxBullets: Int = initialBullets) : Item(world, id, name), Fireable, Usable {
	override val isContinuousUseAllowed = false;
	private var fireCooldown = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	@get:JvmName("canFire")
	val canFire: Boolean
		get() = fireCooldown == 0f && remainingBullets > 0;
	var remainingBullets: Int = initialBullets
		protected set(value) {
			if(value < 0) field = 0;
			else if(value > maxBullets) field = maxBullets;
			else field = value;
		};  // 샷건이라는 하위클래스에서도 사용해야할 것 같아 private를 protected로 변경
	private var cooldownTimer: Task? = null;

	init {
		if(fireInterval < 0f) throw IllegalArgumentException("invalid fire interval");
		if(bulletDamage < 0) throw IllegalArgumentException("invalid bullet damage");
		if(bulletSpeed < 0f) throw IllegalArgumentException("invalid bullet speed");
		if(bulletHP < 0f) throw IllegalArgumentException("invalid bullet HP");
		if(initialBullets < 0f) throw IllegalArgumentException("invalid ammo");
		if(maxBullets < 0f) throw IllegalArgumentException("invalid max ammo");
		if(initialBullets > maxBullets) throw IllegalArgumentException("ammo count can't be greater than max ammo");
	}

	/**
	 * 총에 쿨타임을 건다.
	 */
	protected fun startFireCooldown() {
		if(fireInterval == 0f) return;

		fireCooldown = fireInterval;

		// 남은 쿨타임을 갱신한다. update, delta를 쓰지 않은 이유는 이건 게임 프레임과는 독립적이라고 보기 때문.
		cooldownTimer?.let { Utils.clearInterval(it) };
		cooldownTimer = Utils.setInterval(0.01f) {
			fireCooldown -= 0.01f;
			if(fireCooldown == 0f) {
				cooldownTimer?.let {
					Utils.clearInterval(it);
					cooldownTimer = null;
				};
			}
		};
	}

	/**
	 * 남은 쿨타임을 전체 공격 간격에 비례하여 0.0~1.0로 정규화하여 반환한다.
	 */
	fun getRemainingCooldownPercentage(): Float = fireCooldown / fireInterval;

	/**
	 * 총 쏘기
	 *
	 * @param	target	총알이 향할 좌표
	 * @param	shooter	총알을 쏜 개체
	 * @return	쏜 총알 개수 (실패하면 0)
	 */
	override fun fire(target: Position, shooter: Entity): Int {
		if(!canFire) return 0;

		val bullet = Bullet(world, this, shooter, target, bulletSpeed, bulletDamage, isBulletPenetreble, bulletHP);
		world.addEntity(bullet);
		startFireCooldown();
		remainingBullets--;

		// ammo가 다 떨어진 총은 파괴
		if(remainingBullets == 0) {
			if(holder is Player)
				(world.viewer as? SubtitlesDrawable)?.drawSubtitles("Gun destroyed; no more bullets left", color=Color.SALMON);
			destroy();
		}

		return 1;
	}

	/**
	 * 기본값으로 개체가 보는 방향으로 총을 쏜다.
	 *
	 * @return 성공 여부
	 */
	override fun use(): Boolean {
		val holder: Entity? = this.holder;
		if(holder == null) return false;

		// 개체 회전 각도에 맞는 임의의 위치를 생성한다.
		val radians = toRadians(holder.getRotationAngle() + 90.0);
		val distance = Utils.max(world.width, world.height);  // 그냥 100f 이상 가능한 한 큰 수면 된다.
		val targetX = cos(radians) * distance + holder.x;
		val targetY = sin(radians) * distance + holder.y;
		return fire(Position(targetX.toFloat(), targetY.toFloat()), holder) > 0;
	}

	/**
	 * 쿨타임 해제 타이머 정리
	 */
	override fun cleanUp() {
		cooldownTimer?.let {
			Utils.clearInterval(it);
			cooldownTimer = null;
		};
	}
}
