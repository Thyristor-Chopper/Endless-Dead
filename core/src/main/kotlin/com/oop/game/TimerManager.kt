package com.oop.game;

import com.badlogic.gdx.Gdx;

import com.oop.game.entity.Entity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

private const val MAX_UNIT_TIMER: Float = 1.0f;

/**
 * 지정한 시간마다 특정 작업(타이머)을 실행할 수 있는 오브젝트
 */
class TimerManager {
	private var unitTimer = MAX_UNIT_TIMER;
	private val timers = mutableListOf<Timer>();

	/**
	 * 매 초마다 timers의 타이머들을 갱신하여 대기시간을 줄이고 대기 시간이 0이 된 타이머를 실행한다.
	 */
	internal fun tick(delta: Float) {
		if(unitTimer > 0f) {  // 아직 1초가 안 지났다면
			unitTimer -= delta;
		} else {  // 타이머 갱신
			for(timer in timers) {
				val skip = (timer.onlyInPlay && GameManager.state != GameState.IN_PLAY);
				if(skip) continue;
				
				timer.tick();
			}
			unitTimer = MAX_UNIT_TIMER;
		}
	}
	
	/**
	 * 타이머를 등록한다.
	 *
	 * @param timer	등록할 타이머 객체
	 */
	fun registerTimer(timer: Timer) {
		timers.add(timer);
	}
	
	/**
	 * 타이머를 등록을 해제한다.
	 *
	 * @param timer	제거할 타이머 객체
	 */
	fun unregisterTimer(timer: Timer) {
		timers.remove(timer);
	}
	
	/**
	 * 등록된 모든 타이머를 담은 목록을 반환한다.
	 */
	fun getTimers(): List<Timer> = timers.toList();
}
