package io.potatogun.gdxhelper.entity.manager;

import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

import java.util.function.Consumer;

/**
 * 선형 목록에서 모두 관리하는 기초적인 관리자
 *
 * @param    world           소속 월드
 * @param    capacity        처음 개체 목록 크기
 * @property nearbyThreshold getNearby에서 사용할 가깝다의 기준
 */
class LinearEntityManager(world: World, capacity: Int, private val nearbyThreshold: Float) : ArrayEntityManager(world, capacity) {
	private val addQueue = GdxArray<Entity>(false, 8);
	private val removeQueue = GdxArray<Entity>(false, 8);

	override fun add(entity: Entity): Boolean {
		if(entity.world !== world)
			throw IllegalArgumentException("entity belongs to a different world");
		if(allEntities.contains(entity, true) || addQueue.contains(entity, true)) return false;
		addQueue.add(entity);
		return true;
	}

	override fun remove(entity: Entity): Boolean {
		if(!allEntities.contains(entity, true) || removeQueue.contains(entity, true)) return false;
		removeQueue.add(entity);
		return true;
	}

	override fun update(delta: Float) {
		// 매 프레임 개체 갱신
		super.update(delta);

		// 제거 큐 처리
		if(!removeQueue.isEmpty()) {
			for(i in 0 until removeQueue.size) {
				val entity = removeQueue[i];
				allEntities.removeValue(entity, true);
				entity.dispose();
			}
			removeQueue.clear();
		}

		// 추가 큐 처리
		if(!addQueue.isEmpty()) {
			for(i in 0 until addQueue.size) {
				val entity = addQueue[i];
				allEntities.add(entity);
			}
			addQueue.clear();
		}
	}

	override fun forEachNearby(entity: Entity, callback: Consumer<Entity>) {
		for(it in allEntities)
			if(it.distanceTo(entity) <= nearbyThreshold)
				callback.accept(it);
	}
}
