package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.world.World;

class WeakZombie(world: World, x: Float, y: Float) : Zombie(world, x, y, 21f, 30f, hp = 3, settings = Zombie.Properties().attackDamage(1).speed(150f));
