package io.potatogun.endlessdead.item;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectSet;

import io.potatogun.endlessdead.inventory.Inventory;
import io.potatogun.gdxhelper.util.SharedTextureManager;
import io.potatogun.gdxhelper.util.getGameInstance;
import io.potatogun.gdxhelper.util.loadTexture;

/**
 * 아이템 추상 클래스
 *
 * @property id   아이템 식별자 (소문자)
 * @property name 아이템 이름
 */
abstract class Item @JvmOverloads constructor(id: String, val name: String, settings: Properties = Properties()) {
	/**
	 * 아이템 식별자
	 */
	@get:JvmName("getID")
	val id = id.lowercase();
	/**
	 * 아이템을 들고 있는 인벤토리 (캐시)
	 */
	@set:JvmSynthetic var inventory: Inventory? = null
		internal set;
	/**
	 * 아이템 희귀도
	 */
	val rarity: Rarity;
	/**
	 * 아이템 텍스처 (인벤토리용)
	 *
	 * 아이템 식별자가 텍스처 화일명이 된다.
	 */
	val texture = textures.get(this);

	init {
		settings.fillDefaults();
		rarity = settings.rarity;
	}

	/**
	 * 아이템을 파괴한다.
	 *
	 * @return 성공 여부
	 */
	open fun destroy(): Boolean {
		val inventory = this.inventory;
		if(inventory != null) {
			return inventory.removeItem(this);
		} else {
			if(this is Disposable)
				dispose();
			return true;
		}

		// 나머지는 jvm이나 달빅이 알아서 gc 해주겠지.
	}

	/**
	 * 아이템의 문자열 표현
	 *
	 * @return 문자열 표현
	 */
	override fun toString(): String = name;

	companion object {
		/**
		 * 아이템의 표준 변 길이
		 */
		const val sideLength = 24f;  // 디컴파일해서 확인한 결과 public 자바 field로 바뀜

		val textures: Textures
			inline get() = Textures.getInstance(getGameInstance());
	}

	/**
	 * 아이템 옵션
	 */
	open class Properties {
		@get:JvmSynthetic internal var rarity: Rarity = Rarity.COMMON
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

	/**
	 * 아이템 텍스처 관리자
	 */
	class Textures private constructor(private val game: ApplicationListener) : SharedTextureManager() {
		private val nonExistent = ObjectSet<String>(16);
		val defaultTexture: Texture by lazy { getShared("default") };

		init {
			if(instances.containsKey(game))
				throw IllegalStateException("item texture manger for this game instance has already been created");
			instances.put(game, this);

			register("default", "item/default.bmp");
		}

		/**
		 * 아이템의 텍스처를 불러온다.
		 */
		fun get(item: Item): Texture {
			val itemID = item.id;
			if(hasShared(itemID)) {
				return getShared(itemID);
			} else if(nonExistent.contains(itemID)) {
				return defaultTexture;
			} else {
				try {
					val texture = loadTexture("item/${itemID}.bmp");
					register(itemID, texture);
					return getShared(itemID);
				} catch(e: GdxRuntimeException) {
					nonExistent.add(itemID);
					return defaultTexture;
				}
			}
		}

		override fun dispose() {
			super.dispose();
			instances.remove(game);
		}

		companion object {
			private val instances = IdentityMap<ApplicationListener, Textures>(4);

			fun getInstance(game: ApplicationListener): Textures {
				var instance = instances.get(game);
				if(instance == null)
					instance = Textures(game);
				return instance;
			}

			fun isInstanceCreated(game: ApplicationListener): Boolean = instances.containsKey(game);
		}
	}
}
