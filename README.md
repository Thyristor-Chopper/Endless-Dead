# Endless Dead

코틀린으로 만든 libGDX 기반의 좀비 사냥 슈팅 게임이다.

---

## 요구 사항

자바 1.8 이상 (Windows XP의 경우), Windows 7 이상이라면 자바 17 이상을 추천함.

직접 빌드 시에는 JDK가 필요하다.

Windows XP 32비트 SP3, Windows 7 64비트 SP1, Windows 10 64비트 LTSC 2021에서 동작 확인.

## gdxhelper 서브모듈 불러오기

```
git submodule init && git submodule update
```

## 게임 실행

### 직접 실행 (JDK)

Windows는 `run.bat` 실행

매킨토시나 리눅스는 터미날에서
```bash
./gradlew desktop:run
```

### 컴파일된 JAR 사용

JDK가 설치된 환경에서 `build.bat`를 실행하거나
```bash
./gradlew :desktop:shadowJar
```
로 shadowed JAR를 빌드하고 자바가 설치된 환경에서 `java -jar endless-dead.jar` 하면 된다.

## 조작법

| 글쇠 | 동작 |
|---|---|
| 화살표 / WASD | 플레이어 이동 |
| Esc 또는 P | 일시 중지 |
| 좌클릭 | 아이템 사용 |
| 우클릭 / 사이띄개 | 상자 상호작용 |

## 프로젝트 구조

```
/
├── gdxhelper/   도우미 라이브러리
├── core/        게임 코드 (플랫폼 독립)
└── desktop/     PC용 실행기
```
