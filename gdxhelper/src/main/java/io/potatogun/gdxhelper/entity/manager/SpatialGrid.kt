package io.potatogun.gdxhelper.entity.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongSet;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.pools.EntityArrayPool;
import io.potatogun.gdxhelper.pools.EntitySetPool;
import io.potatogun.gdxhelper.pools.LongSetPool;
import io.potatogun.gdxhelper.util.Math.max2;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

import java.util.function.Consumer;

import kotlin.math.floor;

/**
 * 좌표 분할 격자식 개체 관리자
 *
 * @param    world    소속 월드
 * @param    capacity 처음 개체 목록 크기
 * @property tileSize 타일 크기
 */
class SpatialGrid(world: World, capacity: Int, private val tileSize: Float) : ArrayEntityManager(world, capacity) {
	private val entitiesOfTile = LongMap<GdxArray<Entity>>(capacity * 8);
	private val tilesOfEntity = ObjectMap<Entity, LongSet>(capacity);
	private val addQueue = GdxArray<Entity>(false, 8);
	private val removeQueue = GdxArray<Entity>(false, 8);
	private val hashPool = LongSetPool(8);
	private val tileEntityPool = EntityArrayPool(capacity);
	private val visitedPool = EntitySetPool(capacity / 4);

	override fun add(entity: Entity): Boolean {
		if(entity.world !== world)
			throw IllegalArgumentException("entity belongs to a different world");
		if(tilesOfEntity.containsKey(entity) || addQueue.contains(entity, true)) return false;
		addQueue.add(entity);
		return true;
	}

	override fun remove(entity: Entity): Boolean {
		if(!tilesOfEntity.containsKey(entity) || removeQueue.contains(entity, true)) return false;
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
				val hashes = tilesOfEntity.remove(entity);
				if(hashes != null) {
					val iterator = hashes.iterator();
					while(iterator.hasNext) {
						val hash = iterator.next();
						val entities = entitiesOfTile[hash] ?: continue;
						entities.removeValue(entity, true);
						if(entities.isEmpty()) {
							entitiesOfTile.remove(hash);
							tileEntityPool.free(entities);
						}
					}
					hashPool.free(hashes);
				}
				allEntities.removeValue(entity, true);
				entity.dispose();
			}
			removeQueue.clear();
		}

		// 추가 큐 처리
		if(!addQueue.isEmpty()) {
			for(i in 0 until addQueue.size) {
				val entity = addQueue[i];
				val hashes = hashPool.obtain();
				calculateTileHashes(entity, hashes);

				val iterator = hashes.iterator();
				while(iterator.hasNext) {
					val hash = iterator.next();
					val entities = entitiesOfTile[hash] ?: tileEntityPool.obtain().also { entitiesOfTile.put(hash, it) };
					entities.add(entity);
				}
				tilesOfEntity.put(entity, hashes);
				allEntities.add(entity);
			}
			addQueue.clear();
		}
	}

	override fun updatePosition(entity: Entity) {
		val oldHashes = tilesOfEntity[entity] ?: return;
		val newHashes = hashPool.obtain();
		calculateTileHashes(entity, newHashes);

		// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/utils/LongSet.java
		//   먼저 둘의 크기를 먼저 비교하는 최적화를 하고 원소 내용이 같은지를 비교하기 때문에 안전
		if(oldHashes == newHashes) {
			hashPool.free(newHashes);
			return;
		}

		val oldIterator = oldHashes.iterator();
		while(oldIterator.hasNext) {
			val hash = oldIterator.next();
			if(newHashes.contains(hash)) continue;
			val entities = entitiesOfTile[hash];
			if(entities == null) continue;
			entities.removeValue(entity, true);
			if(entities.isEmpty()) {
				entitiesOfTile.remove(hash);
				tileEntityPool.free(entities);
			}
		}

		val newIterator = newHashes.iterator();
		while(newIterator.hasNext) {
			val hash = newIterator.next();
			if(oldHashes.contains(hash)) continue;
			val entities = entitiesOfTile[hash] ?: tileEntityPool.obtain().also { entitiesOfTile.put(hash, it) };
			entities.add(entity);
		}

		hashPool.free(oldHashes);
		tilesOfEntity.put(entity, newHashes);
	}

	override fun forEachNearby(entity: Entity, callback: Consumer<Entity>) {
		val outerRange = 1;
		val maxHalfLength = max2(entity.width, entity.height) * 0.5f;

		val minTileX = floor((entity.x - maxHalfLength) / tileSize).toInt() - outerRange;
		val maxTileX = floor((entity.x + maxHalfLength) / tileSize).toInt() + outerRange;
		val minTileY = floor((entity.y - maxHalfLength) / tileSize).toInt() - outerRange;
		val maxTileY = floor((entity.y + maxHalfLength) / tileSize).toInt() + outerRange;

		val visited = visitedPool.obtain();
		for(tileX in minTileX..maxTileX)
			for(tileY in minTileY..maxTileY) {
				val hash = (tileX.toLong() shl 32) or (tileY.toLong() and 0xffffffffL);
				val entities = entitiesOfTile[hash] ?: continue;
				for(i in 0 until entities.size) {
					val e = entities[i];
					if(visited.add(e))
						callback.accept(e);
				}
			}
		visitedPool.free(visited);
	}

	private fun calculateTileHashes(entity: Entity, output: LongSet) {
		val maxHalfLength = max2(entity.width, entity.height) * 0.5f;

		val minTileX = floor((entity.x - maxHalfLength) / tileSize).toInt();
		val maxTileX = floor((entity.x + maxHalfLength) / tileSize).toInt();
		val minTileY = floor((entity.y - maxHalfLength) / tileSize).toInt();
		val maxTileY = floor((entity.y + maxHalfLength) / tileSize).toInt();

		output.clear();
		for(tileX in minTileX..maxTileX)
			for(tileY in minTileY..maxTileY) {
				val hash = (tileX.toLong() shl 32) or (tileY.toLong() and 0xffffffffL);
				output.add(hash);
			}
	}
}
