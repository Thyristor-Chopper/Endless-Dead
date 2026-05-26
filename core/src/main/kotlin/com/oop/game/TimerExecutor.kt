package com.oop.game;

import com.badlogic.gdx.Gdx;

import com.oop.game.WorldObject;
import com.oop.game.ZombieGame;
import com.oop.game.entity.Entity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

/**
 * 지정한 시간마다 특정 작업(타이머)을 실행할 수 있는 객체
 */
interface TimerExecutor {
	companion object {
		const val MAX_UNIT_TIMER: Float = 1.0f;
	}
	
	var unitTimer: Float;
	val timers: MutableList<Timer>;
	
	/**
	 * 매 초마다 timers의 타이머들을 갱신하여 대기시간을 줄이고 대기 시간이 0이 된 타이머를 실행한다.
	 */
	fun executeTimers(delta: Float) {
		if(unitTimer > 0.0f) {
			unitTimer -= delta;
			return;
		}
		
		for(timer in timers) {
			var skip = false;
			if(timer.onlyInPlay) {
				if(this is WorldObject && this.world.state != GameState.IN_PLAY) skip = true;
				if(this is World && this.state != GameState.IN_PLAY) skip = true;
			}
			if(skip) continue;
			
			if(timer.canRun) {
				timer.run();
				timer.reset();
			} else {
				timer.tick();
			}
		}
		unitTimer = TimerExecutor.MAX_UNIT_TIMER;
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
	fun getRegisteredTimers(): List<Timer> = timers.toList();
}
