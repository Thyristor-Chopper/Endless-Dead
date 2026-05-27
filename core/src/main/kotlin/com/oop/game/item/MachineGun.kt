package com.oop.game.item

import com.oop.game.world.World

class MachineGun(world: World) : Gun(world, "G003", "Machine Gun", 5, 500f, 2, true, 0.1f, 30, 30) {
    override val allowContinuousUse = true //마우스 키다운으로, 연속발사 가능 구현
}
