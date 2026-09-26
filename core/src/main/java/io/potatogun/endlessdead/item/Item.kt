package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Texture;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.inventory.Inventory;

/**
 * 아이템 추상 클래스
 *
 * @property name     아이템 이름
 * @param    settings 아이템 옵션
 */
abstract class Item @JvmOverloads constructor(val name: String, val texture: Texture = Textures.getShared("default_item"), settings: Properties = Properties()) {
	/**
	 * 아이템을 들고 있는 인벤토리 (캐시)
	 */
	@JvmSynthetic internal var inventory: Inventory? = null;
	/**
	 * 아이템 희귀도
	 */
	val rarity: Rarity;

	init {
		settings.fillDefaults();
		rarity = settings.rarity;
	}

	/**
	 * 아이템을 파괴한다.
	 *
	 * @return 성공 여부
	 */
	fun destroy(): Boolean {
		return inventory?.removeItem(this) ?: true;  // 소유자가 없는 아이템은 그냥 없어지는 것이기 때문에 true로

		// 나머지는 jvm이나 달빅이 알아서 gc 해주겠지.
	}

	/**
	 * 아이템의 문자열 표현
	 *
	 * @return 문자열 표현
	 */
	override fun toString(): String = name;

	/**
	 * 아이템 옵션
	 */
	open class Properties {
		@JvmSynthetic internal var rarity: Rarity = Rarity.COMMON
			private set;

		/**
		 * 희귀도를 지정한다.
		 *
		 * @param rarity 아이템 희귀도
		 * @return 옵션 객체 자신
		 */
		fun rarity(rarity: Rarity): Properties {
			this.rarity = rarity;
			return this;
		}

		@JvmSynthetic internal open fun fillDefaults() {}
	}
}
