package io.potatogun.endlessdead.item;

/**
 * 총잡이 서모너를 (굳이...) 처치했을 때 보상으로 주는 총
 */
class TurboStreamliner : Gun("Turbo Streamliner", Gun.Properties(10, 850f).penetrableBullets().fireInterval(0.02f).rarity(Rarity.RARE)) {
	override val isContinuousUseAllowed = true;
}
