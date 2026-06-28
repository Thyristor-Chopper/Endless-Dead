plugins {
	kotlin("jvm") version "1.9.22" apply false
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
			jvmTarget = "1.8"  // Windows XP 호환용
		}
	}
}
