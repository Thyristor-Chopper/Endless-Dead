package io.potatogun.endlessdead.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

import io.potatogun.endlessdead.Textures;
import io.potatogun.endlessdead.inventory.Inventory;
import io.potatogun.gdxhelper.util.SharedTextureManager;
import io.potatogun.gdxhelper.util.TextureUtils;

/**
 * 아이템 추상 클래스
 *
 * @property id   아이템 식별자 (소문자)
 * @property name 아이템 이름
 */
abstract class Item @JvmOverloads constructor(id: String, val name: String, settings: Properties = Properties()) {
	companion object {
		/**
		 * 재사용하기 위한 아이템 텍스처들이다.
		 */
		@JvmField val textures = ItemTextures();
	}

	/**
	 * 아이템 식별자
	 */
	@get:JvmName("getID")
	val id = id.lowercase();
	/**
	 * 아이템을 들고 있는 인벤토리 (캐시)
	 */
	internal var inventory: Inventory? = null;
	/**
	 * 아이템 희귀도
	 */
	val rarity: Rarity;
	/**
	 * 아이템 텍스처 (인벤토리용)
	 *   ID가 텍스처 화일명이 된다.
	 */
	val texture = textures.get(this);

	init {
		settings.fillDefaults();
		rarity = settings.rarity;
	}

	override fun equals(other: Any?): Boolean = other is Item && other.id == this.id;

	override fun hashCode(): Int = id.hashCode();

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
		internal var rarity: Rarity = Rarity.COMMON
			private set;

		/**
		 * 희귀도를 지정한다.
		 *
		 * @param rarity 아이템 희귀도
		 * @return       옵션 객체 자신
		 */
		fun rarity(rarity: Rarity): Properties {
			this.rarity = rarity;
			return this;
		}

		internal open fun fillDefaults() {}
	}

	class ItemTextures internal constructor() : SharedTextureManager() {
		init {
			register("default", "item/default.bmp");
		}

		/**
		 * 아이템의 텍스처를 불러온다.
		 */
		fun get(item: Item): Texture {
			val itemID = item.id;
			try {
				return getShared(itemID);
			} catch(e: NoSuchElementException) {
				try {
					val texture = TextureUtils.loadTexture("item/${itemID}.bmp");
					register(itemID, texture);
					return getShared(itemID);
				} catch(e: GdxRuntimeException) {
					return getShared("default");
				}
			}
		}
	}
}
