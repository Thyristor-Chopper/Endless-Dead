package com.oop.game;

object ScoreManager {
	var score: Int = 0
		private set;
	
	fun addScore(amount: Int) {
		if(amount < 0) throw IllegalArgumentException("invalid score amount");
		score += amount;
	}
	
	fun resetScore() {
		score = 0;
	}
}
