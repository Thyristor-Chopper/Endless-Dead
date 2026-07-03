plugins {
	kotlin("jvm") version "2.4.0" apply false
}

allprojects {
	repositories {
		gradlePluginPortal()
	}
}

subprojects {
	tasks.withType<JavaCompile>().configureEach {
		// package-info.java 한글 깨짐 방지
		options.encoding = "UTF-8"
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
		compilerOptions {
			// Windows XP 호환성
			jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)

			// -Xlambdas=indy:       코틀린 람다 함수를 invoke 메쏘드가 있는 별도 클래스를 만드는 대신 'invoke dynamic'를 사용한다.
			//                         디컴파일해서 비교했을 때 새 클래스 대신 '메쏘드명$lambda$순번' static method을 만든다.
			// -Xstring-concat=indy: 문자열 조합 관련 최적화. 자바 8에서는 적용되지 않지만 일단은 넣어둠.
			freeCompilerArgs.addAll(listOf("-Xlambdas=indy", "-Xstring-concat=indy", "-Xwarning-level=NOTHING_TO_INLINE:disabled", "-Xwarning-level=UNCHECKED_CAST:disabled"))

			// 자바 인터페이스의 default void f() { ... }문법을 쓴다. 디컴파일해서 비교하니까 DefaultImpls 내부 클래스를 만드는 것보다
			//   훨씬 깔끔하고 효율적이다. (코틀린 1.x은 -Xjvm-default=all)
			jvmDefault = org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode.ENABLE
		}
	}
}
