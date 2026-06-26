plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    api("com.badlogicgames.gdx:gdx:1.12.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
