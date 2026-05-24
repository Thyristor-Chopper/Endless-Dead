package com.oop.game;

import com.oop.game.entity.Entity;
import com.oop.game.item.Item;
import com.oop.game.world.World;

interface TimerExecutor {
	val MAX_UNIT_TIMER: Int;
	var unitTimer: Int;
	val timers: MutableList<Timer>;
	
	fun executeTimers() {
		if(unitTimer != 0) {
			unitTimer--;
			return;
		}
		
		for(timer in timers) {
			var skip = false;
			if(timer.onlyInPlay) {
				if(this is Entity && this.world.state != GameState.IN_PLAY) skip = true;
				if(this is Item && this.world.state != GameState.IN_PLAY) skip = true;
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
	
	fun registerTimer(timer: Timer) {
		timers.add(timer);
	}
	
	fun unregisterTimer(timer: Timer) {
		timers.remove(timer);
	}
}
