plugins {
	kotlin("jvm") version "1.9.22" apply false
	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}
}

subprojects {
	tasks.withType<JavaCompile>().configureEach {
		// package-info.java 한글 깨짐 방지
		options.encoding = "UTF-8"
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			// Windows XP에서 실행하기 위함
			jvmTarget = "1.8"

			// - 설명 -
			//   -Xlambdas=indy:       코틀린 람다 함수를 invoke 메쏘드가 있는 별도 클래스를 만드는 대신 'inline dynamic'를 사용한다.
			//                           디컴파일해서 비교했을 때 새 클래스 대신 '메쏘드명$lambda$순번' static method을 만든다.
			//                           근데 왜 여전히 자바의 Function 람다 x -> { ... }는 안 쓰는지 모르겠지만...
			//   -Xjvm-default=all:    자바 8에서 새로 생긴 인터페이스의 default int f() { ... }문법을 쓴다. 디컴파일해서
			//                           비교하니까 DefaultImpls 내부 클래스를 만드는 것보다 훨씬 깔끔하고 효율적이다.
			//   -Xstring-concat=indy: 문자열 조합 관련 최적화. 자바 8에서는 적용되지 않지만 일단은 넣어둠.
			//                           나중에 누군가 자바 17(9 이상)을 XP에서 작동하게 하는 패치를 만들어 주면 jvmTarget을 올려서 적용 가능
			freeCompilerArgs += listOf("-Xlambdas=indy", "-Xjvm-default=all", "-Xstring-concat=indy")
		}
	}
}
