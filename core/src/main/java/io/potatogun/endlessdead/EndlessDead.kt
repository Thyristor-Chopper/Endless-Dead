package io.potatogun.endlessdead;

import com.badlogic.gdx.utils.ObjectMap;

import io.potatogun.endlessdead.item.Item;
import io.potatogun.endlessdead.screen.Title;
import io.potatogun.endlessdead.screen.ZombieWorldProjector;
import io.potatogun.gdxhelper.Game;
import io.potatogun.gdxhelper.screen.WorldProjector;

/**
 * 좀비 파밍 게임의 메인 본체
 */
class EndlessDead(options: ObjectMap<String, String>) : Game() {
	// 게임 매개변수
	private val seed: Long;
	private val defaultSeed: Boolean;
	private val skipTitle: Boolean;
	// 미리 등록된 스크린. val은 lateinit이 불가하여 lazy 위임 사용.
	@get:JvmSynthetic internal val titleScreen: Title by lazy { Title(this) };
	@get:JvmSynthetic internal val worldProjector: ZombieWorldProjector by lazy { ZombieWorldProjector(this) };

	init {
		/*
		val klass = kotlin.random.Random.Default::class.java;  // Class.forName("kotlin.random.PlatformRandom");
		println("${klass.name}\n");
		klass.declaredMethods.forEach { method ->
			println("${method.name}");
		};
		print("\n");
		klass.declaredFields.forEach { field ->
			println("${field.name}");
		};
		print("\n");
		println(kotlin.random.Random.Default is kotlin.random.Random);
		*/

		/*
		val defaultRandom = Random::class.java.getDeclaredField("Default");
		defaultRandom.isAccessible = true;
		val modifiers = Field::class.java.getDeclaredField("modifiers");
		modifiers.isAccessible = true;
		modifiers.setInt(defaultRandom, defaultRandom.modifiers and Modifier.FINAL.inv());
		defaultRandom.set(null, Random(seed));
		for(i in 1..10)
			println(Random.Default.nextInt(100));
		*/

		// 시드 설정
		val rawSeed = options.get("seed");
		if(rawSeed != null) {
			try {
				seed = rawSeed.toLong();
				defaultSeed = false;
			} catch(e: NumberFormatException) {
				throw IllegalArgumentException("invalid seed");
			}
		} else {
			seed = 0L;
			defaultSeed = true;
		}

		// 타이틀 건너뛰기 설정
		skipTitle = options.containsKey("instant");
	}

	// 게임 시작 시 한 번 호출되는 메쏘드
	override fun create() {
		GameManager.init(this);

		if(!defaultSeed)
			GameManager.setSeed(seed);

		if(skipTitle) {
			GameManager.hideRound = true;
			worldProjector.disableTitleControls();

			GameManager.newGame();
		} else {
			setScreen(titleScreen);
		}

		TitleBar.base = Constants.GAME_TITLE;
	}

	// 게임 시간 갱신
	override fun update(delta: Float) {
		super.update(delta);

		GameManager.update(delta);
	}

	// 자원을 정리한다.
	override fun dispose() {
		super.dispose();
		Resources.dispose();  // 공유 자원 정리
		Item.textures.dispose();
		titleScreen.dispose();  // 이게 로딩이 안 됐을 리가 없다.
		worldProjector.dispose();
		GameManager.player.finalDispose();
	}
}
