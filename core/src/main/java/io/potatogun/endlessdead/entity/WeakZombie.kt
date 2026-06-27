package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.world.World;

class WeakZombie(world: World, x: Float, y: Float) : Zombie(world, x, y, 21f, 30f, settings = Zombie.Properties(3, 1, 150f));
