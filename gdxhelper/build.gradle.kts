plugins {
	kotlin("jvm")
}

dependencies {
	api("com.badlogicgames.gdx:gdx:1.14.1")
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("-parameters")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		javaParameters.set(true)
	}
}
