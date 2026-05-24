plugins {
	id("com.android.application")
    kotlin("android") version "1.9.22"
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:1.10.0")
	runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.10.0:natives-armeabi-v7a")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.10.0:natives-arm64-v8a")
}

android {
    namespace = "com.oop.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.oop.game"
        minSdk = 19  // 안드로이드 4.4 킷캣, 올리지 말 것
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("../core/src/main/resources") 
            java.srcDirs("src/main/kotlin")
        }
    }
}
