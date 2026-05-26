package com.oop.game.entity;

import com.oop.game.world.World;

class NormalZombie(world: World, x: Float, y: Float, player: Player, angle: Float) : Zombie(world, x, y, width=32f, height=45f, hp=5, speed=100f, angle=angle, player=player, attackDamage=3);
