package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.world.World;

class NormalZombie(world: World, x: Float, y: Float) : Zombie(world, x, y, 32f, 45f, hp = 5, settings = Zombie.Properties().attackDamage(3).speed(100f));
