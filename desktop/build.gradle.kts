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
	implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.10.0")  // 1.11.0 이상은 Windows XP에서 OpenAL 초기화 실패 (실행 자체는 되지만 오류 메시지가 도스창에 뜨는 게 거슬리고 나중에 효과음을 추가할 수도 있어서)
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
