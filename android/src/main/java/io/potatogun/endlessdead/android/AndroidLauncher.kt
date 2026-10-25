@file:JvmName("AndroidLauncher")
package io.potatogun.endlessdead.android;

import android.content.Context;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;

class AndroidLauncher : AndroidApplication() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState);

		val game = EndlessDead();

		val config = AndroidApplicationConfiguration().apply {
			useAccelerometer = false;
			useCompass = false;
		};

		initialize(game, config);
	}

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
