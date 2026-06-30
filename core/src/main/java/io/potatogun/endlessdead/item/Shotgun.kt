package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Color;

import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.SubtitlesDrawable;
import io.potatogun.gdxhelper.util.Position;

import kotlin.math.atan2;
import kotlin.math.cos;
import kotlin.math.sin;

/**
 * 샷건
 */
class Shotgun : Gun("shotgun", "Shotgun", Gun.Properties(10, 500f).bulletPenetration(5).fireInterval(1f).bullets(5)) {
	private val spreadAngles = listOf(-0.2f, -0.1f, 0f, 0.1f, 0.2f) //방향 기준 퍼짐 좌표

	override fun shoot(target: Position, shooter: Entity): Int {
		if(!canFire) return 0;

		val centerX = shooter.x;  // shooter, 즉 발사를 하는 주체인 플레이어의 위치를 중심으로 두는 객체
		val centerY = shooter.y;

		val angle = atan2(target.y - centerY, target.x - centerX);  // atan2()으로 사분면(방향)을 확인, -180°부터 +180°까지 정확한 방향 반환

		for(spread in spreadAngles) {
			val finalAngle = angle + spread;  // atan2로 반환한 방향에 list로 저장한 다섯가지 방향으로 퍼짐 구현
			val pelletTarget = Position(
				centerX + cos(finalAngle) * 100f,
				centerY + sin(finalAngle) * 100f  // angle 객체로 각도(방향)을 지정했으니, 그곳의 cos,sin을 이용한 위치 좌표를 구하는 식
			);
			shooter.world.entities.add(Bullet(shooter.world, this, shooter, pelletTarget, bulletSpeed, bulletDamage, isBulletPenetrable, bulletPenetration));
		}

		startFireCooldown();  // 발사간격 함수

		if(!infiniteBullets) {
			remainingBullets -= spreadAngles.size;  // 탄약 수 차감

			// 남은 탄약이 0이 됐을 떄, 무기가 파괴(destroy())되는 효과
			if(remainingBullets == 0) {
				val viewer = shooter.world.viewer;
				if(shooter is Player && viewer is SubtitlesDrawable)
					viewer.drawSubtitles("Shotgun destroyed; no more bullets left", color = Color.SALMON);
				destroy();
			}
		}

		return spreadAngles.size;
	}
}
