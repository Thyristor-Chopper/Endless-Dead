package io.potatogun.gdxhelper.entity.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array as GdxArray;

import io.potatogun.gdxhelper.Window;
import io.potatogun.gdxhelper.collections.View;
import io.potatogun.gdxhelper.collections.createView;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.util.Math.max2;
import io.potatogun.gdxhelper.world.Freezable;
import io.potatogun.gdxhelper.world.World;

/**
 * 배열을 내부적으로 사용하는 개체 관리자
 *
 * @property world 소속 월드
 */
abstract class ArrayEntityManager(@JvmField protected val world: World, capacity: Int) : EntityManager {
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("view")
	override val view: View<Entity> = allEntities.createView();
	/**
	 * 모든 개체 목록을 담는 배열
	 */
	@JvmField protected val allEntities = GdxArray<Entity>(false, capacity);

	override fun draw(batch: SpriteBatch) {
		if(allEntities.isEmpty()) return;

		val halfScreenWidth = Window.width * 0.5f;
		val halfScreenHeight = Window.height * 0.5f;
		val offsetX = world.cameraX;
		val offsetY = world.cameraY;

		for(i in 0 until allEntities.size) {
			val entity = allEntities[i];
			// 보이는 개체만 그리기 (자원 낭비 감소)
			val maxEntityLength = max2(entity.width, entity.height);
			val entityX = entity.x;
			val entityY = entity.y;
			if(entityX >= offsetX - halfScreenWidth - maxEntityLength && entityX <= offsetX + halfScreenWidth + maxEntityLength && entityY >= offsetY - halfScreenHeight - maxEntityLength && entityY <= offsetY + halfScreenHeight + maxEntityLength)
				entity.draw(batch);
		}
	}

	override fun update(delta: Float) {
		for(i in 0 until allEntities.size) {
			val entity = allEntities[i];
			if(world !is Freezable || !world.isFrozen || entity.isUpdatableWhileFrozen)
				entity.update(delta);
			entity.forceUpdate(delta);
		}
	}

	override fun dispose() {
		for(i in 0 until allEntities.size)
			allEntities[i].dispose();
	}
}
