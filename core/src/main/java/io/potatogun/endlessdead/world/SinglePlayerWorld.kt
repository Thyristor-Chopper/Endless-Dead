package io.potatogun.endlessdead.world;

import io.potatogun.endlessdead.entity.Player;

/**
 * 하나의 플레이어가 있는 월드
 */
interface SinglePlayerWorld {
	val player: Player;
}
