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
    override val penetrationDamage = 1;
	override val defaultInvincibleDuration = 0.25f;
	
	override fun update(delta: Float) {
        super.update(delta);  // 부모(LivingGameObject)의 무적 타이머 갱신 로직 실행

        val dx = (world.player.x + world.player.width / 2f - width / 2f) - x;
        val dy = (world.player.y + world.player.height / 2f - height / 2f) - y;
        val distance = sqrt(dx * dx + dy * dy);
		
		// 플레이어의 중심으로 정확히 모이면 어색하니까 살짝은 거리를 두게 하자.
        if(distance > world.player.width * (3f / 4f)) {
            x += dx / distance * speed * delta;
            y += dy / distance * speed * delta;
        } else {
			world.player.takeDamage(attackDamage, attacker=this);
		}
    }
	
	companion object {
		fun weak(world: World, x: Float, y: Float, player: Player, angle: Float) = Zombie(world, x, y, width=21f, height=30f, hp=3, speed=150f, angle=angle, player=player, attackDamage=1);
		fun normal(world: World, x: Float, y: Float, player: Player, angle: Float) = Zombie(world, x, y, width=32f, height=45f, hp=5, speed=100f, angle=angle, player=player, attackDamage=3);
		fun strong(world: World, x: Float, y: Float, player: Player, angle: Float) = Zombie(world, x, y, width=49f, height=70f, hp=15, speed=50f, angle=angle, player=player, attackDamage=5);
	}
}
