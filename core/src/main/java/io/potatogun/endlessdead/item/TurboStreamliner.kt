package io.potatogun.endlessdead.item;

import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.position.Position;
import io.potatogun.gdxhelper.util.max2;

/**
 * 총잡이 서모너를 (굳이...) 처치했을 때 보상으로 주는 총
 */
class TurboStreamliner : Gun("turbo_streamliner", "Turbo Streamliner", Gun.Properties(7, 850f).penetrableBullets().fireInterval(0.04f).maxBullets(500).rarity(Rarity.RARE)) {
	override val isContinuousUseAllowed = true;
	@Suppress("INAPPLICABLE_JVM_NAME")
	@get:JvmName("isAutoDestroyable")
	override val autoDestroy = false;
	private var refillTimer = 0f;
	private val refillInterval = 60f;

	override fun update(delta: Float) {
		super.update(delta);

		if(refillTimer > 0f) {
			refillTimer = max2(refillTimer - delta, 0f);
			if(refillTimer <= 0f)
				bullets = maxBullets;
		}
	}

	override fun shoot(target: Position, shooter: Entity): Int {
		val result = super.shoot(target, shooter);

		// 1분마다 총알 재장전
		if(bullets <= 0)
			refillTimer = refillInterval;

		return result;
	}
}
