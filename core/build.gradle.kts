plugins {
	kotlin("jvm")
	id("org.jetbrains.dokka-javadoc")
}

dependencies {
	implementation(project(":gdxhelper"))
}
