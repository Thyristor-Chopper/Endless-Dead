# Endless Dead

LibGDX 기반의 고정 화면 아케이드 게임과 프레임워크가 포함되어 있다.

---

## 1. 사전 준비

### Java JDK 17 (권장) 또는 JDK 1.8 이상 (필수, Windows XP의 경우)
Mac:
```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
    /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

Windows / Linux: <https://adoptium.net/> 에서 Temurin 17 설치.

확인:
```bash
java -version    # 17.x.x 가 보이면 OK
```

### IntelliJ IDEA Community Edition (권장)
<https://www.jetbrains.com/idea/download/> 에서 다운로드.

---

## 2. 게임 실행

프로젝트 루트에서:
```bash
./gradlew desktop:run        # macOS / Linux
gradlew.bat desktop:run      # Windows
```

또는 Windows는 `run.bat` 실행

---

## 3. 조작법

| 키 | 동작 |
|---|---|
| 화살표 또는 WASD | 플레이어 이동 |
| ESC | 일시 중지 |
| 좌클릭 | 아이템 사용 |
| 우클릭 또는 사이띄개 | 상자 상호작용 |

플레이어의 체력이 모두 소진되면 Game Over.

---

## 4. 프로젝트 구조

```
/
├── gdxhelper/   프레임워크
├── core/        플랫폼 독립 게임 코드
└── desktop/     데스크톱 전용 런처
```

---

## 5. 자주 나오는 질문

**Q. 실행하면 "GLFW may only be used on the main thread" 에러가 뜬다.**
A. macOS 에서 `-XstartOnFirstThread` JVM 옵션이 필요한데, Gradle `application` 플러그인이 자동으로 적용한다. **Gradle 패널의 `run` task**로 실행하거나, 직접 main으로 실행하려면 Run Configuration의 VM options에 `-XstartOnFirstThread` 추가.

**Q. JDK 17 대신 21을 써도 되나?**
A. 보통 동작하지만 검증된 건 17. 문제 생기면 17로 맞추자.

**Q. 이미지가 화면에 안 보인다.**
A. PNG 파일이 `core/src/main/resources/`에 있는지 확인. Gradle 빌드(`./gradlew core:processResources`)가 한 번 돌아야 클래스패스에 올라간다.
