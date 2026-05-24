package com.oop.game;

class Timer(timerInterval: Int, val onlyInPlay: Boolean = false, internal val run: () -> Unit) {
	val interval = timerInterval - 1;
	private var delta = interval
		set(value) {
			if(value < 0) field = 0;
			else if(value > interval) field = interval;
			else field = value;
		};
	
	val canRun: Boolean
		get() = (delta == 0);
	
	// TimerExecutor(외부)에서 접근하기 위해 어쩔 수 없이 internal
	internal fun tick() {
		delta--;
	}
	
	internal fun reset() {
		delta = interval;
	}
}
