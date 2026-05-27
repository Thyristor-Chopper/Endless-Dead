package com.oop.game;

import com.badlogic.gdx.Gdx;

import com.oop.game.GameObject;
import com.oop.game.entity.Entity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

import java.util.WeakHashMap;

// 원래는 객체의 private 필드로써 기능하는 변수들
// 전역 private 맵으로 한 이유:
//   이것은 내부 데이타로 원래 외부에서 접근하면 안 되지만
//   interface는 private/protected 필드를 지원하지 않기 때문.
// 또한 WeakHashMap를 쓴 이유는 removeDead에 의해 클래스가 더 이상 사용되지 않더라도
//   일반 맵을 쓰면 해당 객체를 키로 아직 갖고 있어서 계속 메모리에 남아있고 gc가 안 돼서 그렇다.
private val unitTimers = WeakHashMap<TimerExecutor, Float>();
private val timers = WeakHashMap<TimerExecutor, MutableList<Timer>>();
private const val MAX_UNIT_TIMER: Float = 1.0f;

/**
 * 지정한 시간마다 특정 작업(타이머)을 실행할 수 있는 객체
 */
interface TimerExecutor {
	/**
	 * 매 초마다 timers의 타이머들을 갱신하여 대기시간을 줄이고 대기 시간이 0이 된 타이머를 실행한다.
	 */
	fun executeTimers(delta: Float) {
		if(unitTimers.getOrPut(this, { MAX_UNIT_TIMER }) > 0f) {  // 아직 1초가 안 지났다면
			val currentTimerValue = unitTimers[this] ?: MAX_UNIT_TIMER;
			unitTimers[this] = currentTimerValue - delta;
		} else {  // 타이머 갱신
			for(timer in timersOf(this)) {
				val skip = (timer.onlyInPlay && this is GameObject && this.game.state != GameState.IN_PLAY);
				if(skip) continue;
				
				timer.tick();
			}
			unitTimers[this] = MAX_UNIT_TIMER;
		}
	}
	
	/**
	 * 타이머를 등록한다.
	 *
	 * @param timer	등록할 타이머 객체
	 */
	fun registerTimer(timer: Timer) {
		val timerList = timersOf(this);
		timerList.add(timer);
	}
	
	/**
	 * 타이머를 등록을 해제한다.
	 *
	 * @param timer	제거할 타이머 객체
	 */
	fun unregisterTimer(timer: Timer) {
		val timerList = timersOf(this);
		timerList.remove(timer);
	}
	
	/**
	 * 등록된 모든 타이머를 담은 목록을 반환한다.
	 */
	fun getTimers(): List<Timer> = timersOf(this).toList();
}

private inline fun timersOf(timerExecutor: TimerExecutor) = timers.getOrPut(timerExecutor, { mutableListOf<Timer>() });
