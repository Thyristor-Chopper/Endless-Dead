package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.GameManager;
import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.entity.Bullet;
import io.potatogun.endlessdead.entity.ItemSelectable;
import io.potatogun.endlessdead.entity.Landmine;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.gdxhelper.entity.Entity;
import io.potatogun.gdxhelper.screen.drawSubtitles;

import java.lang.Math.toRadians;

import kotlin.math.cos;
import kotlin.math.sin;

/**
 * 지뢰 설치기
 */
class LandminePlacer : Item("landmine_placer", "Landmine Placer", Item.Properties().rarity(Rarity.UNCOMMON)), LimitedUsable {
	override val isContinuousUseAllowed = false;
	/**
	 * 지뢰 피해량
	 */
	val landmineDamage = 3;
	/**
	 * 지뢰 지속 시간(초)
	 */
	val landmineHealth = 600;
	/**
	 * 최대 사용 횟수
	 */
	override val maxUses = 10;
	/**
	 * 남은 지뢰 개수
	 */
	override var remainingUses = maxUses
		private set;

	/**
	 * 총을 든 개체가 보는 방향으로 총을 쏜다.
	 *
	 * @return 성공 여부
	 */
	override fun use(user: ItemSelectable): Boolean {
		if(user.selectedItem !== this) return false;
		if(user !is Entity) return false;

		val world = user.getWorld();
		val radians = toRadians(user.getRotationAngle() + 90.0).toFloat();
		val distance = 32f;
		val targetX = cos(radians) * distance + user.x;
		val targetY = sin(radians) * distance + user.y;

		world.entities.add(Landmine(world, targetX, targetY, user, landmineDamage, landmineHealth));
		remainingUses--;
		if(remainingUses <= 0) {
			if(user is Player)
				world.projector?.drawSubtitles("Landmine placer broken; no more mines left", color=Color.SALMON);
			destroy();
		}
		return true;
	}
}
