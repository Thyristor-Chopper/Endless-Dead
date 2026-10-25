pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
	}
}

rootProject.name = "endless-dead"

include("gdxhelper", "core", "desktop")

val currentJavaVersion = JavaVersion.current().majorVersion.toIntOrNull() ?: 0
if(currentJavaVersion >= 17) {
	include("android")
}
