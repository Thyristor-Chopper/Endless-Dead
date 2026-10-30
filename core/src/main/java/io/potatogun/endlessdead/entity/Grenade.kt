package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.EndlessDead;
import io.potatogun.endlessdead.Pools;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.cos;
import kotlin.math.sin;

/**
 * 유탄
 *
 * @param    world               유탄이 있는 세계
 * @param    shooter             쏜 개체
 * @param    target              유탄이 향할 위치
 * @param    speed               유탄 속도
 * @property shrapnelSpeed       유탄 폭발 시 나오는 총알들의 속도
 * @property shrapnelDamage      유탄 폭발 시 나오는 총알들의 피해량
 * @property shrapnelPenetration 유탄 폭발 시 나오는 총알들의 관통력
 */
class Grenade @JvmOverloads constructor(world: World, shooter: Entity, target: Position?, speed: Float, private val shrapnelSpeed: Float, private val shrapnelDamage: Int, private val shrapnelPenetration: Int = Int.MAX_VALUE) : Bullet(world, shooter, target, speed, 0, false, 1, 24f, EndlessDead.instance.resources.textures.getShared("grenade")) {
	private val grenadeShooter = shooter;  // private val shooter로 하려 했는데 코틀린 진짜 X같은 언어가 필드 섀도잉는 또 왜 쳐 막는데 진짜 욕나오는 언어네 ㅡㅡ;

	override fun onCollide(entity: LivingEntity): Boolean {
		explode();
		return false;
	}

	private inline fun explode() {  // onCollidePreProcess에서 한 번만 쓰이므로 인라인
		val world = level;
		if(world == null) return;
		val x = this.x;
		val y = this.y;
		val radius = 128f;
		val bulletPosition = Pools.position.obtain(x, y);
		val bulletTarget = Pools.position.obtain();
		var i = 0f;
		val maxAngle = (2f * Math.PI).toFloat();
		while(i < maxAngle) {  // 코틀린 이 X같은 언어 왜 for(초기식; 조건식; 증감식) 없어 어떤 놈이 없애자고 했어
			val targetX = x + radius * cos(i);
			val targetY = y + radius * sin(i);
			bulletTarget.set(targetX, targetY);
			world.entities.add(Bullet(world, grenadeShooter, bulletTarget, shrapnelSpeed, shrapnelDamage, true, shrapnelPenetration, initialPosition = bulletPosition).apply { teleport(x, y) });
			i += 0.2618f;
		}
		Pools.position.free(bulletTarget);
		Pools.position.free(bulletPosition);
		this.remove();
	}
}
