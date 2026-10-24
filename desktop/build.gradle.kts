import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val buildDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmm"))

plugins {
	kotlin("jvm")
	id("org.jetbrains.dokka-javadoc")
	id("com.gradleup.shadow") version "8.3.11"  // 자바 8을 지원하는 마지막 버전
	application
}

dependencies {
	implementation(project(":core"))
	implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:1.14.2")
	implementation("com.badlogicgames.gdx:gdx-platform:1.14.2:natives-desktop")
}

application {
	mainClass.set("io.potatogun.endlessdead.desktop.DesktopLauncher")

	// macOS에서 LWJGL3 실행 시 필요
	applicationDefaultJvmArgs = listOf("-XX:+IgnoreUnrecognizedVMOptions", "-XstartOnFirstThread")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		freeCompilerArgs.addAll(listOf("-Xno-param-assertions"))
	}
}

tasks.shadowJar {
	archiveBaseName.set(rootProject.name)
	archiveClassifier.set("")
	archiveVersion.set(buildDate)

	mergeServiceFiles()

	manifest {
		attributes["Main-Class"] = application.mainClass.get()
	}
}
