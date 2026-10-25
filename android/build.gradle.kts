import java.util.Properties
import java.io.FileInputStream
import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
}

val localProperties = Properties().apply {
    val propertiesFile = file("local.properties")
    if(propertiesFile.exists()) {
        load(FileInputStream(propertiesFile))
    }
}

android {
	namespace = "io.potatogun.endlessdead"

	// 안드로이드 16
	compileSdk = 36

	defaultConfig {
		applicationId = "io.potatogun.endlessdead"

		// 안드로이드 4.4부터 16까지
		minSdk = 19
		targetSdk = 36

		versionCode = 1
		versionName = "1.0.0"

		// 안드로이드 4.4에서 필요
		multiDexEnabled = true

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	compileOptions {
		// 안드로이드 킷캣 호환
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8

		// 이전 안드로이드 버전(API < 26) 호환
		isCoreLibraryDesugaringEnabled = true
	}

	sourceSets {
		getByName("main") {
			assets.srcDirs("src/main/resources", "../core/src/main/resources", "../gdxhelper/src/main/resources")
			jniLibs.srcDirs("src/main/jniLibs")
		}
	}

	packaging {
        resources {
			excludes.add("/textures/**")
		}
	}

	signingConfigs {
		create("release") {
			storeFile = file("endless-dead-key.jks")
			storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD")
			keyAlias = "endless-dead-key"
			keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD")

			enableV1Signing = true
			enableV2Signing = true
			enableV3Signing = true
		}
	}

	buildTypes {
		release {
			signingConfig = signingConfigs.getByName("release")
			isMinifyEnabled = false
		}
	}

	applicationVariants.all {
		outputs
			.map { it as ApkVariantOutputImpl }
			.forEach { output ->
				val appName = rootProject.name
				val variantName = name
				val version = versionName

				output.outputFileName = "${appName}-${variantName}-v${version}.apk"
			}
	}
}

val natives by configurations.creating

dependencies {
	implementation(project(":core"))
	implementation("com.badlogicgames.gdx:gdx-backend-android:1.13.1")  // 1.13.5는 롤리팝 이상 요구
	natives("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-armeabi-v7a")
	natives("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-arm64-v8a")
	natives("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86")
	natives("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86_64")
	implementation("androidx.multidex:multidex:2.0.1")
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
}

tasks.register("copyAndroidNatives") {
	val outputDir = file("src/main/jniLibs")
	inputs.files(natives)
	outputs.dir(outputDir)
	doLast {
		outputDir.mkdirs()
		natives.files.forEach { jar ->
			val dir = when {
				jar.name.endsWith("natives-arm64-v8a.jar") -> file("$outputDir/arm64-v8a")
				jar.name.endsWith("natives-armeabi-v7a.jar") -> file("$outputDir/armeabi-v7a")
				jar.name.endsWith("natives-x86_64.jar") -> file("$outputDir/x86_64")
				jar.name.endsWith("natives-x86.jar") -> file("$outputDir/x86")
				else -> null
			}
			if(dir == null) return@forEach
			dir.mkdirs()
			copy {
				from(zipTree(jar))
				into(dir)
				include("*.so")
			}
		}
	}
}

tasks.named("preBuild") {
	dependsOn("copyAndroidNatives")
}
