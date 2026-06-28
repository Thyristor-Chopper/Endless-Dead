plugins {
	kotlin("jvm") version "1.9.22" apply false
	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}
}

subprojects {
	tasks.withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"  // Windows XP에서 실행하기 위함
		}
	}
}
