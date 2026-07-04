plugins {
	kotlin("jvm")
}

dependencies {
	implementation(project(":gdxhelper"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		freeCompilerArgs.addAll(listOf("-Xwarning-level=NOTHING_TO_INLINE:disabled", "-Xwarning-level=UNCHECKED_CAST:disabled"))
	}
}
