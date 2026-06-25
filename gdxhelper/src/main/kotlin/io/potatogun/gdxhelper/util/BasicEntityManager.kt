package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.entity.Entity;

class BasicEntityManager : EntityManager {
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

	override fun dispose() {
		for(entity in entities)
			entity.dispose();
	}
}
