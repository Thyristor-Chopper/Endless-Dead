package io.potatogun.endlessdead.item

import com.badlogic.gdx.graphics.Color

import io.potatogun.endlessdead.Position
import io.potatogun.endlessdead.entity.Bullet
import io.potatogun.endlessdead.entity.Entity
import io.potatogun.endlessdead.world.World

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Shotgun(world: World) : Gun(world, "shotgun", "Shotgun", 5, 500f, 5, true, 1f, 10, 10) {
    private val spreadAngles = listOf(-0.2f, -0.1f, 0f, 0.1f, 0.2f)

    override fun fire(target: Position, shooter: Entity): Boolean {
        if(!canFire) return false

        val centerX = shooter.x + shooter.width / 2f
        val centerY = shooter.y + shooter.height / 2f

        val angle = atan2(target.y - centerY, target.x - centerX) //360 돌리는 거 구글링

        for(spread in spreadAngles) {
            val finalAngle = angle + spread
            val pelletTarget = Position(
                centerX + cos(finalAngle) * 100f,
                centerY + sin(finalAngle) * 100f //구글링했음
            )

            world.addEntity(Bullet(world, this, shooter, pelletTarget, bulletSpeed, bulletDamage, penetrable, bulletHp))
        }

        startFireCooldown()
        ammo -= spreadAngles.size

        if(ammo == 0) {
            if(shooter === world.player)
                world.drawSubtitles("Shotgun destroyed; no more bullets left", color = Color.SALMON)
            destroy()
        }

        return true
    }
}
