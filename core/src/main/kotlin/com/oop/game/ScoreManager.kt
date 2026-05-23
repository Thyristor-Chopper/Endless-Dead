package com.oop.game;

object ScoreManager {
	var score: Int = 0
		private set(value) {
			if(value < 0) field = 0;
			else field = value;
		};
	
	fun addScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score += amount;
	}
	
	fun subtractScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score -= amount;
	}
	
	fun resetScore() {
		score = 0;
	}
}
