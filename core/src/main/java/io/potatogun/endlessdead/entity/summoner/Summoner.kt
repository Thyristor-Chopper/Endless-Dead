package io.potatogun.endlessdead.entity.summoner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.Player;
import io.potatogun.endlessdead.world.SinglePlayerWorld;
import io.potatogun.gdxhelper.entity.rotateToRandom;
import io.potatogun.gdxhelper.entity.manager.getClosestOf;
import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.nextFloat;
import io.potatogun.gdxhelper.world.World;

/**
 * 플레이어를 보면서 일정한 간격으로 개체를 소환하는 기계
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
	/**
	 * 이 소환기에 대한 타이머
	 *
	 * 자바에서도 클래스 내부 구현에서는 직접 필드 접근이 자연스럽기 때문에 @JvmField가 있다.
	 */
	@JvmField protected val timers = TimerManager();
	/**
	 * 이 소환기가 현재 활성 상태인지의 여부
	 */
	open val isActive: Boolean
		get() = true;
	/**
	 * 개체 소환 간격
	 */
	abstract val summonInterval: Float;

	init {
		rotateToRandom();

		Gdx.app.postRunnable {  // abstract val은 생성자에서 바로 읽으면 0이 됨 abstract val로 하나 명시적 게터 함수로 하나 어차피 바이트코드는 둘 다 getter 함수가 되는데 뭐 이래 하 코틀린 진짜...
			timers.register(RepeatingTimer(summonInterval) {
				if(isActive)
					summon();
			});
		};
	}

	override fun update(delta: Float) {
		super.update(delta);

		timers.update(delta);

		val player = findPlayer();
		if(isActive && player != null)
			rotateTo(player);
	}

	/**
	 * 개체를 소환한다.
	 *
	 * 활성 상태 여부는 이 공통 클래스에서 검사하므로 구현체는 소환 로직만 구현하면 된다.
	 */
	protected abstract fun summon();

	/**
	 * 주변의 플레이어를 찾는다.
	 *
	 * @return 가장 가까운 플레이어
	 */
	protected inline fun findPlayer(): Player? {
		val world = this.world;
		return if(world is SinglePlayerWorld) world.player else world.entities.getClosestOf<Player>(this);
	}
}
