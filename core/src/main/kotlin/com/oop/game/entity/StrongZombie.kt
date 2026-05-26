package com.oop.game.entity;

import com.oop.game.world.World;

class StrongZombie(world: World, x: Float, y: Float, player: Player, angle: Float) : Zombie(world, x, y, width=49f, height=70f, hp=15, speed=50f, angle=angle, player=player, attackDamage=5);
