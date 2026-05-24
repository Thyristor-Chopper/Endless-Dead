package com.oop.game.item;

interface Usable {
	val allowContinuousUse: Boolean;
	
	fun use(): Boolean;
}
