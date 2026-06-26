package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.world.World;

class NormalZombie(world: World, x: Float, y: Float) : Zombie(world, x, y, 32f, 45f, hp = 6, settings = Zombie.Properties(3, 100f));
