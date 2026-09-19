plugins {
	kotlin("jvm")
	id("org.jetbrains.dokka-javadoc")
}

dependencies {
	api(project(":gdxhelper"))
}
