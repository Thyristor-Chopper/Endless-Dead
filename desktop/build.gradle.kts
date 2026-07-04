plugins {
	kotlin("jvm")
	id("com.gradleup.shadow") version "8.3.11"  // 자바 8을 지원하는 마지막 버전
	application
}

dependencies {
	implementation(project(":gdxhelper"))
	implementation(project(":core"))
	implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.10.0")  // 1.11.0 이상은 Windows XP에서 소리 관련 오류 발생
	implementation("com.badlogicgames.gdx:gdx-platform:1.14.2:natives-desktop")
}

application {
	mainClass.set("io.potatogun.endlessdead.desktop.DesktopLauncher")

	// macOS에서 LWJGL3 실행 시 필요
	if(System.getProperty("os.name").lowercase().contains("mac")) {
		applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
	}
}

tasks.shadowJar {
	archiveBaseName.set(rootProject.name)
	archiveClassifier.set("")
	archiveVersion.set("")

	mergeServiceFiles()

	manifest {
		attributes["Main-Class"] = application.mainClass.get()
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		// 외부 API 없음
		freeCompilerArgs.addAll(listOf("-Xno-param-assertions"))
	}
}
