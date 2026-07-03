package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer.Task;

import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.InventoryHolder;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.screen.drawSubtitles;
import io.potatogun.gdxhelper.timer.Tasks;
import io.potatogun.gdxhelper.util.Math.max2;

import java.lang.Math.toRadians;

import kotlin.math.cos;
import kotlin.math.sin;

/**
 * 총 아이템 추상 클래스
 *
 * @param id       총 식별자
 * @param name     총 이름
 * @param settings 총 옵션
 * @throws IllegalArgumentException	총 옵션이 잘못된 경우
 */
abstract class Gun(id: String, name: String, settings: Properties) : Item(id, name, settings), Shootable, Usable {
	override val isContinuousUseAllowed = false;
	/**
	 * 총알 피해량
	 */
	val bulletDamage: Int;
	/**
	 * 총알 속도
	 */
	val bulletSpeed: Float;
	/**
	 * 총알 관통력
	 */
	val bulletPenetration: Int;
	/**
	 * 총알 관통 가능 여부
	 */
	@JvmField val isBulletPenetrable: Boolean;
	/**
	 * 발사 속도
	 */
	val fireInterval: Float;
	/**
	 * 최대 총알 개수
	 */
	@JvmField val maxBullets: Int;
	/**
	 * 무한 총알 여부
	 */
	@JvmField val infiniteBullets: Boolean;
	/**
	 * 총알 지름
	 */
	private val bulletSize: Float;
	/**
	 * 총알 텍스처
	 */
	private val bulletTexture: Texture;
	private var fireCooldown = 0f
		set(value) {
			if(value < 0f) field = 0f;
			else field = value;
		};
	/**
	 * 현재 쏘기 가능 여부
	 */
	@get:JvmName("canFire")
	val canFire: Boolean
		get() = fireCooldown == 0f && (infiniteBullets || remainingBullets > 0);
	/**
	 * 남은 총탄 개수
	 */
	var remainingBullets: Int = 0  // 생성자에서 다시 초기화됨
		protected set(value) {
			if(value < 0) field = 0;
			else field = value;
		};  // 샷건이라는 하위클래스에서도 사용해야할 것 같아 private를 protected로 변경
	/**
	 * 총 쏘기 쿨타임
	 */
	private var cooldownTimer: Task? = null;
	/**
	 * 남은 쿨타임을 전체 공격 간격에 비례하여 0.0~1.0로 정규화하여 반환한다.
	 * 
	 * @return 정규화된 값
	 */
	val remainingCooldownPercentage: Float
		get() = if(fireInterval == 0f) 0f else fireCooldown / fireInterval;

	init {
		settings.fillDefaults();
		bulletDamage = settings.bulletDamage;
		bulletSpeed = settings.bulletSpeed;
		bulletPenetration = settings.bulletPenetration;
		isBulletPenetrable = settings.isBulletPenetrable;
		fireInterval = settings.fireInterval;
		maxBullets = settings.maxBullets;
		remainingBullets = settings.bullets;
		infiniteBullets = settings.isBulletsInfinite;
		bulletSize = settings.bulletSize;
		bulletTexture = settings.bulletFaceTexture;
	}

	/**
	 * 총에 쿨타임을 건다.
	 */
	protected fun startFireCooldown() {
		if(fireInterval == 0f) return;

		fireCooldown = fireInterval;

		// 남은 쿨타임을 갱신한다. update, delta를 쓰지 않은 이유는 이건 게임 프레임과는 독립적이라고 보기 때문.
		cooldownTimer?.cancel();
		cooldownTimer = Tasks.setInterval(0.01f, { GameManager.isPlaying }) {
			fireCooldown -= 0.01f;
			if(fireCooldown == 0f) {
				cooldownTimer?.let {
					it.cancel();
					cooldownTimer = null;
				};
			}
		};
	}

	/**
	 * 총 쏘기
	 *
	 * @param target  총알이 향할 좌표
	 * @param shooter 총알을 쏜 개체
	 * @return        쏜 총알 개수 (실패하면 0)
	 */
	override fun shoot(target: Position, shooter: Entity): Int {
		if(!canFire) return 0;

		val world = shooter.world;
		val bullet = Bullet(world, this, shooter, target, bulletSpeed, bulletDamage, isBulletPenetrable, bulletPenetration, bulletSize, bulletTexture).apply { if(shooter is LivingEntity) team = shooter.team };
		world.entities.add(bullet);
		startFireCooldown();

		if(!infiniteBullets) {
			remainingBullets--;

			// ammo가 다 떨어진 총은 파괴
			if(remainingBullets == 0) {
				if(shooter is Player)
					world.projector?.drawSubtitles("Gun destroyed; no more bullets left", color=Color.SALMON);
				destroy();
			}
		}

		return 1;
	}

	/**
	 * 총을 든 개체가 보는 방향으로 총을 쏜다.
	 *
	 * @return 성공 여부
	 */
	override fun use(user: InventoryHolder): Boolean {
		if(!user.inventory.hasItem(this)) return false;
		if(user !is Entity) return false;

		// 개체 회전 각도에 맞는 임의의 위치를 생성한다.
		val radians = toRadians(user.getRotationAngle() + 90.0);
		val distance = max2(user.world.width, user.world.height);  // 그냥 100f 이상 가능한 한 큰 수면 된다.
		val targetX = cos(radians) * distance + user.x;
		val targetY = sin(radians) * distance + user.y;
		return shoot(Position(targetX.toFloat(), targetY.toFloat()), user) > 0;
	}

	/**
	 * 총 아이템 옵션
	 *
	 * @property bulletDamage 총탄 피해량 (0 이상)
	 * @property bulletSpeed  총탄 속도 (0이면 지뢰처럼 안 움직임, 음수면 역방향으로 발사)
	 * @throws IllegalArgumentException 속성이 잘못된 경우
	 */
	open class Properties(@JvmField val bulletDamage: Int, @JvmField val bulletSpeed: Float) : Item.Properties() {
		internal var bulletPenetration = 1
			private set;
		internal var isBulletPenetrable = false
			private set;
		internal var fireInterval = 0f
			private set;
		internal var bullets = 0
			private set;
		internal var maxBullets = 0
			private set;
		internal var isBulletsInfinite = true
			private set;
		internal var bulletSize = 16f
			private set;
		internal lateinit var bulletFaceTexture: Texture
			private set;

		init {
			if(bulletDamage < 0) throw IllegalArgumentException("invalid bullet damage");
		}

		/**
		 * 총알 관통력을 지정한다. 자동으로 penetrableBullets도 호출된다.
		 *
		 * @param penetration 총알 관통력
		 * @return            옵션 객체 자신
		 */
		fun bulletPenetration(penetration: Int): Properties {
			if(penetration <= 0) throw IllegalArgumentException("invalid value");
			this.bulletPenetration = penetration;
			this.isBulletPenetrable = true;
			return this;
		}

		/**
		 * 총알이 관통 가능하게 한다.
		 *
		 * @return 옵션 객체 자신
		 */
		fun penetrableBullets(): Properties {
			this.isBulletPenetrable = true;
			return this;
		}

		/**
		 * 발사 속도를 지정한다.
		 *
		 * @param fireInterval 발사 속도
		 * @return             옵션 객체 자신
		 */
		fun fireInterval(fireInterval: Float): Properties {
			if(fireInterval < 0f) throw IllegalArgumentException("invalid value");
			this.fireInterval = fireInterval;
			return this;
		}

		/**
		 * 처음 총알 개수를 지정하고 발사 가능한 총알 수를 제한한다.
		 *
		 * @param bullets 총알 개수
		 * @return        옵션 객체 자신
		 */
		fun bullets(bullets: Int): Properties {
			if(bullets <= 0) throw IllegalArgumentException("invalid value");
			if(bullets > maxBullets) maxBullets = bullets;
			this.bullets = bullets;
			if(maxBullets == 0) maxBullets = bullets;
			isBulletsInfinite = false;
			return this;
		}

		/**
		 * 최대 총알 개수를 지정한다.
		 *
		 * @param bullets 최대 총알 개수
		 * @return        옵션 객체 자신
		 */
		fun maxBullets(bullets: Int): Properties {
			if(bullets <= 0) throw IllegalArgumentException("invalid value");
			this.maxBullets = bullets;
			if(this.bullets == 0) this.bullets = bullets;
			isBulletsInfinite = false;
			return this;
		}

		/**
		 * 총알 크기를 지정한다. 
		 *
		 * @param size 총알 지름
		 * @return     옵션 객체 자신
		 */
		fun bulletSize(size: Float): Properties {
			if(size <= 0) throw IllegalArgumentException("invalid value");
			this.bulletSize = size;
			return this;
		}

		/**
		 * 총알 텍스처를 지정한다.
		 *
		 * @param texture 텍스처
		 * @return        옵션 객체 자신
		 */
		fun bulletTexture(texture: Texture): Properties {
			bulletFaceTexture = texture;
			return this;
		}

		override fun fillDefaults() {
			super.fillDefaults();
			if(!::bulletFaceTexture.isInitialized)
				bulletFaceTexture = Textures.getShared("bullet");
		}
	}
}
