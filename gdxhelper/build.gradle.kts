plugins {
	kotlin("jvm") version "1.9.22"
}

dependencies {
	api("com.badlogicgames.gdx:gdx:1.14.1")
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	compilerOptions {
		javaParameters.set(true)
	}
}
