package io.potatogun.endlessdead.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.rotateToRandom;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.nextFloat;
import io.potatogun.gdxhelper.world.World;

/**
 * 플레이어를 보면서 개체를 소환하는 기계
 *
 * @param world   개체가 속한 세계
 * @param name    개체 이름
 * @param x       개체의 X 위치
 * @param y       개체의 Y 위치
 * @param width   가로 크기
 * @param height  세로 크기
 * @param health  최대 체력
 * @param texture 개체의 텍스처
 */
abstract class Summoner @JvmOverloads constructor(world: World, name: String, x: Float, y: Float, width: Float, height: Float, health: Int, texture: Texture? = null) : LivingEntity(world, name, x, y, width, height, health, texture) {
	@JvmField protected val timers = TimerManager();
	open val isActive: Boolean
		get() = true;
	abstract val summonInterval: Float;

	init {
		rotateToRandom();

		Gdx.app.postRunnable {  // abstract val은 생성자에서 바로 읽으면 0이 됨 하 코틀린 진짜...
			timers.register(RepeatingTimer(summonInterval) {
				if(isActive)
					summon();
			});
		};
	}

	override fun update(delta: Float) {
		super.update(delta);

		timers.tick(delta);

		val player = findPlayer();
		if(isActive && player != null)
			rotateTo(player);
	}

	protected abstract fun summon();

	protected inline fun findPlayer(): Player? {
		val world = this.world;
		return if(world is SinglePlayerWorld) world.player else world.entities.getClosestOf<Player>(this);
	}
}
