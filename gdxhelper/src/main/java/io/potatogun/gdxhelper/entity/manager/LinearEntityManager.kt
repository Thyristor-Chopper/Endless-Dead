package io.potatogun.gdxhelper.entity.manager;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

/**
 * 선형 목록에서 모두 관리하는 기초적인 관리자
 *
 * @param    world           소속 월드
 * @param    capacity        처음 개체 목록 크기
 * @property nearbyThreshold getNearby에서 사용할 가깝다의 기준
 */
class LinearEntityManager(world: World, capacity: Int, private val nearbyThreshold: Float) : ArrayEntityManager(world, capacity) {
	override fun add(entity: Entity): Boolean {
		if(entity.world !== world)
			throw IllegalArgumentException("entity belongs to a different world");
		if(allEntities.any { it === entity }) return false;
		allEntities.add(entity);
		return true;
	}

	override fun remove(entity: Entity): Boolean {
		entity.dispose();
		return allEntities.removeValue(entity, true);
	}

	override fun forEachNearby(entity: Entity, callback: (Entity) -> Unit) {
		for(it in allEntities)
			if(it.distanceTo(entity) <= nearbyThreshold)
				callback(it);
	}
}
