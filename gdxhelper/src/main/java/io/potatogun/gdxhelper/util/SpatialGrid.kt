package io.potatogun.gdxhelper.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array as GdxArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongSet;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

import kotlin.math.floor;
import kotlin.random.Random;
import kotlin.reflect.KClass;

/**
 * 좌표 분할 격자식 개체 관리자
 *
 * @property tileSize 타일 크기
 */
class SpatialGrid(private val world: World, private val tileSize: Float) : EntityManager {
	private val allEntities = GdxArray<Entity>(false, 16);
	private val entitiesOfTile = LongMap<GdxArray<Entity>>();
	private val tilesOfEntity = ObjectMap<Entity, LongSet>();
	private val addQueue = GdxArray<Entity>();
	private val removeQueue = GdxArray<Entity>();
	private val longSetPool = object : Pool<LongSet>() {
		override fun newObject(): LongSet = LongSet();

		override fun reset(obj: LongSet) {
			obj.clear();
		}
	};
	private val arrayPool = object : Pool<GdxArray<Entity>>() {
		override fun newObject(): GdxArray<Entity> = GdxArray(false, 8);

		override fun reset(obj: GdxArray<Entity>) {
			obj.clear();
		}
	};
	private val nearbyVisited = ObjectSet<Entity>();

	override fun add(entity: Entity): Boolean {
		if(tilesOfEntity.containsKey(entity) || addQueue.contains(entity, true)) return false;
		addQueue.add(entity);
		return true;
	}

	override fun remove(entity: Entity): Boolean {
		if(!tilesOfEntity.containsKey(entity) || removeQueue.contains(entity, true)) return false;
		removeQueue.add(entity);
		return true;
	}

	override fun draw(batch: SpriteBatch) {
		if(allEntities.isEmpty()) return;
		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		val offsetX = world.offsetX;
		val offsetY = world.offsetY;

		for(i in 0 until allEntities.size) {
			val entity = allEntities[i];
			// 보이는 개체만 그리기 (자원 낭비 감소)
			val maxEntityLength = Utils.max2(entity.width, entity.height);
			val entityX = entity.x;
			val entityY = entity.y;
			if(entityX >= offsetX - halfScreenWidth - maxEntityLength && entityX <= offsetX + halfScreenWidth + maxEntityLength && entityY >= offsetY - halfScreenHeight - maxEntityLength && entityY <= offsetY + halfScreenHeight + maxEntityLength)
				entity.draw(batch);
		}
	}

	override fun update(delta: Float) {
		// 매 프레임 개체 갱신
		for(i in 0 until allEntities.size) {
			val entity = allEntities[i];
			val world = entity.world;
			if(world !is Freezable || !world.isFrozen || entity.isUpdatableWhileFrozen)
				entity.update(delta);
			entity.forceUpdate(delta);
		}

		// 제거 큐 처리
		if(!removeQueue.isEmpty()) {
			for(i in 0 until removeQueue.size) {
				val entity = removeQueue[i];
				val hashes = tilesOfEntity.remove(entity);
				if(hashes != null) {
					val iterator = hashes.iterator();
					while(iterator.hasNext) {
						val hash = iterator.next();
						val list = entitiesOfTile.get(hash) ?: continue;
						list.removeValue(entity, true);
						if(list.isEmpty()) {
							entitiesOfTile.remove(hash);
							arrayPool.free(list);
						}
					}
					longSetPool.free(hashes);
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
				val hashes = longSetPool.obtain();
				calculateTileHashes(entity, hashes);

				val iterator = hashes.iterator();
				while(iterator.hasNext) {
					val hash = iterator.next();
					var list = entitiesOfTile.get(hash);
					if(list == null) {
						list = arrayPool.obtain();
						entitiesOfTile.put(hash, list);
					}
					list.add(entity);
				}
				tilesOfEntity.put(entity, hashes);
				allEntities.add(entity);
			}
			addQueue.clear();
		}
	}

	override fun updatePosition(entity: Entity) {
		val oldHashes = tilesOfEntity[entity] ?: return;
		val newHashes = longSetPool.obtain();
		calculateTileHashes(entity, newHashes);

		if(oldHashes.size == newHashes.size) {
			var identical = true;
			val iterator = newHashes.iterator();
			while(iterator.hasNext)
				if(!oldHashes.contains(iterator.next())) {
					identical = false;
					break;
				}
			if(identical) {
				longSetPool.free(newHashes);
				return;
			}
		}

		val oldIterator = oldHashes.iterator();
		while(oldIterator.hasNext) {
			val hash = oldIterator.next();
			if(newHashes.contains(hash)) continue;
			val entities = entitiesOfTile.get(hash);
			if(entities == null) continue;
			entities.removeValue(entity, true);
			if(entities.isEmpty()) {
				entitiesOfTile.remove(hash);
				arrayPool.free(entities);
			}
		}

		val newIterator = newHashes.iterator();
		while(newIterator.hasNext) {
			val hash = newIterator.next();
			if(oldHashes.contains(hash)) continue;
			var entities = entitiesOfTile.get(hash);
			if(entities == null) {
				entities = arrayPool.obtain();
				entitiesOfTile.put(hash, entities);
			}
			entities.add(entity);
		}

		longSetPool.free(oldHashes);
		tilesOfEntity.put(entity, newHashes);
	}

	override fun getNearby(entity: Entity): GdxArray<Entity> {
		val ret = GdxArray<Entity>(false, 32);
		nearbyVisited.clear();

		val outerRange = 1;
		val maxHalfLength = Utils.max2(entity.width, entity.height) * 0.5f;

		val minTileX = floor((entity.x - maxHalfLength) / tileSize).toInt() - outerRange;
		val maxTileX = floor((entity.x + maxHalfLength) / tileSize).toInt() + outerRange;
		val minTileY = floor((entity.y - maxHalfLength) / tileSize).toInt() - outerRange;
		val maxTileY = floor((entity.y + maxHalfLength) / tileSize).toInt() + outerRange;

		for(tileX in minTileX..maxTileX)
			for(tileY in minTileY..maxTileY) {
				val hash = (tileX.toLong() shl 32) or (tileY.toLong() and 0xffffffffL);
				val entities = entitiesOfTile.get(hash) ?: continue;
				for(i in 0 until entities.size) {
					val entity = entities[i];
					if(nearbyVisited.add(entity))
						ret.add(entity);
				}
			}

		return ret;
	}

	override fun getAll(): GdxArray<Entity> = allEntities;

	override fun dispose() {
		for(i in 0 until allEntities.size)
			allEntities[i].dispose();
	}

	private fun calculateTileHashes(entity: Entity, output: LongSet) {
		val maxHalfLength = Utils.max2(entity.width, entity.height) * 0.5f;

		val minTileX = floor((entity.x - maxHalfLength) / tileSize).toInt();
		val maxTileX = floor((entity.x + maxHalfLength) / tileSize).toInt();
		val minTileY = floor((entity.y - maxHalfLength) / tileSize).toInt();
		val maxTileY = floor((entity.y + maxHalfLength) / tileSize).toInt();

		for(tileX in minTileX..maxTileX)
			for(tileY in minTileY..maxTileY) {
				val hash = (tileX.toLong() shl 32) or (tileY.toLong() and 0xffffffffL);
				output.add(hash);
			}
	}

	override fun getRandom(): Entity? {
		if(allEntities.isEmpty()) return null;
		return allEntities[Random.nextInt(allEntities.size)];
	}

	override fun <T : Entity> countOf(type: KClass<T>): Int {
		var ret = 0;
		for(i in 0 until allEntities.size)
			if(type.isInstance(allEntities[i]))
				ret++;
		return ret;
	}

	override fun <T : Entity> getFirstOf(type: KClass<T>): T? {
		for(i in 0 until allEntities.size) {
			val entity = allEntities[i];
			if(type.isInstance(entity))
				return entity as T;
		}
		return null;
	}

	override fun <T : Entity> getRandomOf(type: KClass<T>): T? {
		var count = 0;
		var ret: T? = null;
		for(i in 0 until allEntities.size) {
			val entity = allEntities[i];
			if(type.isInstance(entity) && Random.nextInt(++count) == 0)
				ret = entity as T;
		}
		return ret;
	}

	override fun getClosest(entity: Entity): Entity? {
		if(allEntities.isEmpty()) return null;
		var ret = allEntities[0];
		var min = ret.distanceTo(entity);
		for(i in 0 until allEntities.size) {
			val e = allEntities[i];
			val distance = e.distanceTo(entity);
			if(distance < min) {
				min = distance;
				ret = e;
			}
		}
		return ret;
	}

	override fun <T : Entity> getClosestOf(entity: Entity, type: KClass<T>): T? {
		if(allEntities.isEmpty()) return null;
		var ret: T? = null;
		var min = Utils.max2(world.width, world.height);
		for(i in 0 until allEntities.size) {
			val e = allEntities[i];
			if(!type.isInstance(e)) continue;
			val distance = e.distanceTo(entity);
			if(distance < min) {
				min = distance;
				ret = e as T;
			}
		}
		return ret;
	}

	override fun getDistanceSorted(entity: Entity): List<Entity> = allEntities.sortedWith(compareBy { it.distanceTo(entity) });
}
