package com.oop.game;

/**
 * 게임의 현재 상태를 나타내는 열거형.
 *
 * Boolean 깃발(isGameOver) 대신 enum 을 쓰는 이유:
 *   ▸ 상태 가짓수가 늘어날 때 깔끔히 확장 가능 (예: PAUSED, MENU, VICTORY)
 *   ▸ when 으로 분기하면 'else' 없이 모든 상태를 다뤘는지 컴파일러가 체크해줌
 *   ▸ 코드를 읽을 때 "이 게임에 어떤 상태들이 있는가" 가 한눈에 보임
 *   (7주차에서 배우는 enum class 의 전형적 활용)
 */
enum class GameState {
	IN_PLAY,
	PAUSED,
	GAME_OVER;
}
