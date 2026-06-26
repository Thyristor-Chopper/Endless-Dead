package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * 선형 목록에서 모두 관리하는 초보적인 관리자
 *
 * @property nearbyStandard getNearby에서 사용할 가깝다의 기준
 */
class BasicEntityManager(private val nearbyStandard: Float) : EntityManager {
	private val entities = mutableListOf<Entity>();

	override fun add(entity: Entity): Boolean {
		if(entities.any { it === entity }) return false;
		entities.add(entity);
		return true;
	}

	override fun remove(entity: Entity): Boolean {
		entity.dispose();
		return entities.remove(entity);
	}

	override fun getAll(): List<Entity> = entities.toList();

	override fun getNearby(entity: Entity): List<Entity> {
		val ret = mutableListOf<Entity>();
		for(it in entities)
			if(it.distanceTo(entity) <= nearbyStandard)
				ret.add(it);
		return ret;
	}

	override fun dispose() {
		for(entity in entities)
			entity.dispose();
	}
}
