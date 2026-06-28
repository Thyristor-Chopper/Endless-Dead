plugins {
	kotlin("jvm") version "1.9.22"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	application
}

dependencies {
	implementation(project(":gdxhelper"))
	implementation(project(":core"))
	implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.10.0")  // 1.11.0 이상은 Windows XP에서 소리 관련 오류 발생
	implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-desktop")
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
