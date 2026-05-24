package com.oop.game;

interface TimerExecutor {
	val MAX_UNIT_TIMER: Int;
	var unitTimer: Int;
	val timers: MutableList<Timer>;
	
	fun executeTimers() {
		if(unitTimer == 0) {
			for(timer in timers) {
				if(timer.canRun) {
					timer.run();
					timer.reset();
				} else {
					timer.tick();
				}
			}
			unitTimer = MAX_UNIT_TIMER;
		} else {
			unitTimer--;
		}
	}
	
	fun registerTimer(timer: Timer) {
		timers.add(timer);
	}
	
	fun unregisterTimer(timer: Timer) {
		timers.remove(timer);
	}
}
