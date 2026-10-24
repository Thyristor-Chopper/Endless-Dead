package io.potatogun.endlessdead.spawner;

import com.badlogic.gdx.Gdx;

import io.potatogun.gdxhelper.timer.RepeatingTimer;
import io.potatogun.gdxhelper.timer.TimerManager;
import io.potatogun.gdxhelper.util.Updatable;
import io.potatogun.gdxhelper.world.World;

/**
 * 일정한 간격으로 개체를 소환하는 (보이지 않는) 시스템적 객체
 *
 * @property world    속하는 월드
 * @param    interval 개체 소환 간격
 */
abstract class Spawner(@JvmField protected val world: World, interval: Float) : Updatable {
	/**
	 * 이 소환기에 대한 타이머
	 *
	 * 자바에서도 클래스 내부 구현에서는 직접 필드 접근이 자연스럽기 때문에 `@JvmField`가 있다.
	 */
	@JvmField protected val timers = TimerManager();

	init {
		timers.register(RepeatingTimer(interval, this::spawn));  // abstract val은 생성자에서 바로 읽으면 0이 됨
	}

	override fun update(delta: Float) {
		timers.update(delta);
	}

	/**
	 * 개체를 소환한다.
	 */
	protected abstract fun spawn();
}
