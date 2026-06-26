package io.potatogun.gdxhelper.util;

import io.potatogun.gdxhelper.Utils;
import io.potatogun.gdxhelper.entity.Entity;

import kotlin.math.floor;

/**
 * 해시를 사용하는 좌표 분할 격자식 개체 관리자
 *
 * @property tileSize 타일 크기
 */
class SpatialHashGridEntityManager(private val tileSize: Float) : EntityManager {
	private val entitiesOfTile = mutableMapOf<Long, MutableList<Entity>>();
	private val tilesOfEntity = mutableMapOf<Entity, Set<Long>>();

	override fun add(entity: Entity): Boolean {
		if(entity in tilesOfEntity) return false;
		val hashes = getTileHashes(entity);
		for(hash in hashes)
			entitiesOfTile.getOrPut(hash)
				{ mutableListOf<Entity>() }
				.add(entity);
		tilesOfEntity[entity] = hashes;
		return true;
	}

	override fun remove(entity: Entity): Boolean {
		val hashes = tilesOfEntity[entity] ?: return false;
		tilesOfEntity.remove(entity);
		for(hash in hashes) {
			val list = entitiesOfTile[hash] ?: continue;
			list.remove(entity);
			if(list.isEmpty())
				entitiesOfTile.remove(hash);
		}
		entity.dispose();
		return true;
	}

	override fun updatePosition(entity: Entity) {
		val oldHashes = tilesOfEntity[entity] ?: return;
		val newHashes = getTileHashes(entity);
		if(oldHashes == newHashes) return;

		for(hash in oldHashes) {
			if(hash in newHashes) continue;
			val list = entitiesOfTile[hash] ?: continue;
			list.remove(entity);
			if(list.isEmpty())
				entitiesOfTile.remove(hash);
		}

		for(hash in newHashes) {
			if(hash in oldHashes) continue;
			entitiesOfTile.getOrPut(hash)
				{ mutableListOf<Entity>() }
				.add(entity);
		}
		tilesOfEntity[entity] = newHashes;
	}

	override fun getNearby(entity: Entity): List<Entity> {
		val outerRange = 1;
		val halfWidth = entity.width * 0.5f;
		val halfHeight = entity.height * 0.5f;
		val maxHalfLength = Utils.max2(halfWidth, halfHeight);  // 개체가 회전됐을 때를 고려

		val minTileX = floor((entity.x - maxHalfLength) / tileSize).toInt() - outerRange;
		val maxTileX = floor((entity.x + maxHalfLength) / tileSize).toInt() + outerRange;

		val minTileY = floor((entity.y - maxHalfLength) / tileSize).toInt() - outerRange;
		val maxTileY = floor((entity.y + maxHalfLength) / tileSize).toInt() + outerRange;

		val ret = mutableSetOf<Entity>();
		for(tileX in minTileX..maxTileX)
			for(tileY in minTileY..maxTileY)
				entitiesOfTile[getTileHash(tileX, tileY)]?.let { ret.addAll(it) };
		return ret.toList();
	}

	override fun getAll(): List<Entity> = tilesOfEntity.keys.toList();

	override fun dispose() {
		for(entity in tilesOfEntity.keys)
			entity.dispose();
	}

	/**
	 * 개체가 속한 타일 좌표의 해시를 계산한다. (중심만)
	 * 
	 * @param entity 기준 개체
	 */
	private inline fun getTileHash(entity: Entity) = getTileHash(floor(entity.x / tileSize).toInt(), floor(entity.y / tileSize).toInt());

	/**
	 * 타일 좌표의 해시를 계산한다.
	 * 
	 * @param tileX 타일 X 좌표
	 * @param tileY 타일 Y 좌표
	 */
	private fun getTileHash(tileX: Int, tileY: Int): Long = (tileX.toLong() shl 32) or (tileY.toLong() and 0xffffffffL);

	/**
	 * 지정한 개체의 크기와 위치에 맞는 모든 타일 좌표 해시를 구한다.
	 * 
	 * @param entity 기준 개체
	 */
	private fun getTileHashes(entity: Entity): Set<Long> {
		val halfWidth = entity.width * 0.5f;
		val halfHeight = entity.height * 0.5f;
		val maxHalfLength = Utils.max2(halfWidth, halfHeight);  // 개체가 회전됐을 때를 고려

		val minTileX = floor((entity.x - maxHalfLength) / tileSize).toInt();
		val maxTileX = floor((entity.x + maxHalfLength) / tileSize).toInt();

		val minTileY = floor((entity.y - maxHalfLength) / tileSize).toInt();
		val maxTileY = floor((entity.y + maxHalfLength) / tileSize).toInt();

		val ret = mutableSetOf<Long>();
		for(tileX in minTileX..maxTileX)
			for(tileY in minTileY..maxTileY)
				ret.add(getTileHash(tileX, tileY));
		return ret;
	}
}
