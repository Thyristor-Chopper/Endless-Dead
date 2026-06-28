package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.gdxhelper.util.Timer;
import io.potatogun.gdxhelper.util.TimerManager;
import io.potatogun.gdxhelper.world.World;

/**
 * 스스로 행동하고 타겟을 정하여 공격하는 개체
 *
 * @param world   개체가 속한 세계
 * @param name    개체 표시 이름
 * @param x       개체의 처음 X 위치
 * @param y       개체의 처음 Y 위치
 * @param width   가로 크기
 * @param height  세로 크기
 * @param health  최대 체력
 * @param texture 개체 텍스처(없을 수도 있음)
 */
abstract class Mob(world: World, name: String, x: Float, y: Float, width: Float, height: Float, initialHealth: Int, texture: Texture?) : LivingEntity(world, name, x, y, width, height, initialHealth, texture) {
	/**
	 * 공격 대상
	 */
	var target: LivingEntity? = null
		get() {
			val target: LivingEntity? = field;
			if(!isValidTarget(target)) {
				val newTarget: LivingEntity? = findNewTarget();
				field = if(isValidTarget(newTarget)) newTarget else null;
				return field;
			}
			return target;
		}
		protected set;
	/**
	 * 개체 감지 범위 (0: 제한 없음)
	 */
	open val followRange = 0f;

	/**
	 * 새 공격 대상을 찾는 로직
	 */
	protected abstract fun findNewTarget(): LivingEntity?;

	/**
	 * 올바른 공격 대상인지의 여부
	 */
	private inline fun isValidTarget(entity: LivingEntity?): Boolean = entity != null && entity.isAlive && !entity.isInvincible && !isSameTeamWith(entity) && (followRange == 0f || (followRange > 0f && distanceTo(entity) <= followRange));

	/**
	 * 공격 대상에게 대미지를 입힌다.
	 *
	 * @return 성공 여부
	 */
	fun damageTarget(): Boolean = target?.let { damageTarget(it) } ?: false;
}
