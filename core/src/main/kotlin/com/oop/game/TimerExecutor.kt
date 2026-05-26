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
	val MAX_UNIT_TIMER: Int
		get() {
			if(this is World)
				return this.game.fps;
			else if(this is WorldObject)
				return this.world.game.fps;
			// 우리 게임에서 TimerExecutor는 World, Item, Entity에만 있기 때문에 원칙적으로 아래 코드는 실행되면 안 된다.
			// 하지만 확실한 예외 처리를 위해 현재 실제 렌더링되는 fps를 반환한다. 하지만 render()/update()는 타깃 fps에 맞춰지기 때문에 원래는 정확하지 않다.
			return Gdx.graphics.getFramesPerSecond();
		};
	var unitTimer: Int;
	val timers: MutableList<Timer>;
	
	/**
	 * 매 초마다 timers의 타이머들을 갱신하여 대기시간을 줄이고 대기 시간이 0이 된 타이머를 실행한다.
	 */
	fun executeTimers() {
		if(unitTimer != 0) {
			unitTimer--;
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
		unitTimer = MAX_UNIT_TIMER;
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
