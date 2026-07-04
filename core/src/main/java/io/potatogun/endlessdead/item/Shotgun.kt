package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Color;

import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.screen.drawSubtitles;

import kotlin.math.atan2;
import kotlin.math.cos;
import kotlin.math.sin;

/**
 * 산탄총 아이템 구현체
 */
class Shotgun : Gun("shotgun", "Shotgun", Gun.Properties(10, 500f).bulletPenetration(5).fireInterval(1f).bullets(5)) {
	companion object {
		private val spreadAngles = floatArrayOf(-0.2f, -0.1f, 0.1f, 0.2f);  // 방향 기준 퍼짐 좌표
	}

	override fun shoot(target: Position, shooter: Entity): Int {
		val world = shooter.getWorld();
		val centerX = shooter.x;  // shooter, 즉 발사를 하는 주체인 플레이어의 위치를 중심으로 두는 객체
		val centerY = shooter.y;
		val angle = atan2(target.y - centerY, target.x - centerX);  // atan2()으로 사분면(방향)을 확인, -180°부터 +180°까지 정확한 방향 반환
		for(spread in spreadAngles) {  // 디컴파일해서 확인한 결과 C언어 스타일의 인덱스 기반 iteration으로 바뀜
			if(!canFire) break;
			val finalAngle = angle + spread;  // atan2로 반환한 방향에 배열로 저장한 다섯 가지 방향으로 퍼짐 구현
			val pelletTarget = Position(  // angle로 각도(방향)을 지정했으니, 그곳의 cos,sin을 이용한 위치 좌표를 구하는 식
				centerX + cos(finalAngle) * 100f,
				centerY + sin(finalAngle) * 100f
			);
			world.entities.add(Bullet(world, this, shooter, pelletTarget, bulletSpeed, bulletDamage, isBulletPenetrable, bulletPenetration));
			if(!infiniteBullets)
				remainingBullets--;  // 탄약 수 차감
		}

		val defaultShoot = super.shoot(target, shooter);

		return defaultShoot + spreadAngles.size;
	}
}
