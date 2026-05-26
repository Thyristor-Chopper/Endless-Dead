package com.oop.game.entity;

import com.oop.game.world.World;

import kotlin.math.sqrt;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  적 예제 — enemy.png 이미지, 수평으로 자동 왕복 이동.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  GameObject 를 상속해 만든 '입력 없이 스스로 움직이는' 객체 예제.
 *
 *  핵심 포인트:
 *   ▸ update() 에 입력 처리가 없다 — AI(자율 행동)는 여기서 작성.
 *   ▸ direction 이라는 '상태 변수'를 둬서 좌/우 방향 전환을 구현.
 *
 *  응용 아이디어:
 *   ▸ 생성자에서 speed 를 받아 FastEnemy, SlowEnemy 로 다양화
 *   ▸ 체력(hp)과 takeDamage() 메서드 추가
 *   ▸ 이동 패턴을 사인파, 원운동 등으로 바꾸기
 *
 * @param minX 왕복 이동의 왼쪽 한계 (보통 0f)
 * @param maxX 왕복 이동의 오른쪽 한계 (보통 worldWidth)
 * @param minY
 * @param maxY
 */
open class Zombie(world: World, x: Float, y: Float, width: Float, height: Float, hp: Int, val attackDamage: Int, private val angle: Float, private val player: Player, private val speed: Float = 100f, texture: String = "zombie.bmp") : LivingEntity(world, x, y, width, height, texture, hp) {
    override fun update(delta: Float) {
        super.update(delta);  // 부모(LivingGameObject)의 무적 타이머 갱신 로직 실행

        val dx = (world.player.x + Player.PLAYER_WIDTH / 2.0f - width / 2.0f) - x;
        val dy = (world.player.y + Player.PLAYER_HEIGHT / 2.0f - height / 2.0f) - y;
        val distance = sqrt(dx * dx + dy * dy);
        if (distance > 0f) {
            x += dx / distance * speed * delta;
            y += dy / distance * speed * delta;
        }
		
		if(collidesWith(world.player))
			world.player.takeDamage(attackDamage, attacker=this);
    }
}
