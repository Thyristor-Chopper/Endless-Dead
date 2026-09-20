plugins {
	kotlin("jvm")
	id("org.jetbrains.dokka-javadoc") version "2.2.0"
}

dependencies {
	api(project(":gdxhelper"))
}
