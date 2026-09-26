@file:JvmName("EntityUtils")
package io.potatogun.endlessdead.entity;

import io.potatogun.endlessdead.Pools;
import io.potatogun.gdxhelper.entity.Entity;

import java.util.function.Consumer;

/**
 * 지정한 대상과 같은 팀인지 확인한다.
 *
 * @param entity 비교 대상
 * @return 같은 팀 여부
 */
@JvmName("isSameTeam") inline fun Entity.isSameTeamWith(entity: Entity): Boolean = (this is TeamMember && entity is TeamMember && team != null && entity.team != null && team == entity.team);

/**
 * 주변 개체를 순회하면서 작업을 실행한다. (코틀린 전용)
 *
 * @param callback 실행할 서브루틴
 */
@JvmSynthetic inline fun Entity.forEachNearby(callback: (Entity) -> Unit) {
	val world = getWorld();
	val nearbyEntities = Pools.entityArray.obtain();
	try {
		world.entities.getNearby(this, nearbyEntities);
		for(i in 0 until nearbyEntities.size) {
			val entity = nearbyEntities[i];
			callback(entity);
		}
	} finally {
		Pools.entityArray.free(nearbyEntities);
	}
}

/**
 * 주변 개체를 순회하면서 작업을 실행한다. (자바 전용)
 *
 * 람다가 인라인되지 못하고 쓸 데 없이 변수 캡처 등이 발생할 수 있으므로 사용을 권장하지 않는다.
 *
 * @param callback 실행할 서브루틴
 */
fun Entity.forEachNearby(callback: Consumer<Entity>) {
	forEachNearby(callback::accept);
}
