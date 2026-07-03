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

			// -Xlambdas=indy: 코틀린 람다 함수를 invoke 메쏘드가 있는 별도 클래스를 만드는 대신 'invoke dynamic'를 사용한다. 디컴파일해서 비교했을 때 '메쏘드명$lambda$순번' static method을 만든다.
			// -Xstring-concat=indy: 문자열 조합 관련 최적화. 자바 8에서는 적용되지 않지만 일단은 넣어둠.
			// -Xno-call-assertions: 자바 함수 호출 반환 값에 대해 null 검사를 제거한다. 이 정도는 내가 알아서 한다. (-Xno-param-assertions는 안전성을 위해 생략)
			// -Xno-receiver-assertions: 인라인 함수나 확장함수 호출 시 매개변수 null 검사를 제거한다. 어차피 함수 body 내에서 null 검사를 또 수행하며 인라인 함수는 컴파일러가 먼저 잡는다.
			freeCompilerArgs.addAll(listOf("-Xlambdas=indy", "-Xstring-concat=indy", "-Xno-call-assertions", "-Xno-receiver-assertions", "-Xno-source-debug-extension", "-Xwarning-level=NOTHING_TO_INLINE:disabled", "-Xwarning-level=UNCHECKED_CAST:disabled"))

			// 자바 인터페이스의 default void f() { ... }문법을 쓴다. 디컴파일해서 비교하니까 DefaultImpls 내부 클래스를 만드는 것보다
			//   훨씬 깔끔하고 효율적이다. (코틀린 1.x은 -Xjvm-default=all)
			jvmDefault = org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode.ENABLE
		}
	}
}
