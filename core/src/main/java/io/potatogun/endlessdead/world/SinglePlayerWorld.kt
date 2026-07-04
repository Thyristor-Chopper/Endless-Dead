package io.potatogun.endlessdead.world;

import io.potatogun.endlessdead.entity.Player;

/**
 * 하나의 플레이어가 있는 월드
 */
interface SinglePlayerWorld {
	/**
	 * 이 월드의 플레이어
	 *
	 * 자바에서는 getPlayer() 사용
	 */
	val player: Player;
}
