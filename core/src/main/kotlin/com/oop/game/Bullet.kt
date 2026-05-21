package com.oop.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

class Bullet(
	world: GameWorld,
    x: Float,
    y: Float,
    /*target은 총알의 목적지, 마우스 좌클릭 위치를 저장 시도 예정*/
    val target: Position,
    private val speed: Float,
    val damage: Int
) : GameObject(world, x, y, 3f, 24f) {
	private val texture = Texture(Gdx.files.internal("bullet.png"))
	
    var alive = true

    var distance = bulletTarget(this, target)
    var dx = target.x - x
    var dy = target.y - y

	override fun update(delta: Float) {
		if (distance > 0f) {
			x += dx / distance * speed * delta
			y += dy / distance * speed * delta
		}
	}

	override fun draw(batch: SpriteBatch) {
		batch.draw(texture, x, y, width, height);
	}

	fun isAlive(): Boolean {
		return alive
	}

	fun kill() {
		alive = false
	}
}
