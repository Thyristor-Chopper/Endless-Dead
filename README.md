# Endless Dead

코틀린으로 만든 libGDX 기반의 좀비 사냥 슈팅 게임이다.

안드로이드 버전은 아직 불완전하다(화상 조이스틱 등 화면접촉식 장치용 UI 미구현). 따라서 아직은 외장 하드웨어 자판이 연결돼있어야 할 수 있다.

범용 라이브러리 gdx-helper는 `gdxhelper/` 디렉토리 내에 깃 서브모듈 형태로 참조돼있으며 빌드에 요구된다.

---

## 요구 사항

### PC

자바 1.8 이상 (직접 빌드 시에는 JDK 필요)

Windows XP 32비트 SP3(JDK 1.8u51), Windows 7 64비트 SP1(JDK 17)에서 빌드 가능 확인.

Windows XP 32비트 SP3, Windows 7 64비트 SP1, Windows 10 64비트 21H2에서 정상 실행 확인. 팀으로 개발했을 당시에는 다른 개발자의 컴퓨터로 Windows 11 64비트에서도 실행 가능함을 확인했었다.

### Android

안드로이드 4.4 이상 (현재 target SDK는 36: 안드로이드 16이다.)

안드로이드 4.4.2(갤럭시 노트2), 6.0.1(갤럭시 S7), 8.0(갤럭시 S7), 16(갤럭시 S7 커스텀 펌웨어)에서 정상 실행 확인.

## gdxhelper 서브모듈 불러오기

```
git submodule init && git submodule update
```

## 게임 실행

### PC

#### 직접 실행 (JDK)

JDK 1.8 이상이 필요하다.

Windows는 `run.bat` 실행

매킨토시나 리눅스는 터미날에서
```bash
./gradlew desktop:run
```

#### 컴파일된 JAR 사용

JDK 1.8 이상이 설치된 환경에서 `build.bat`를 실행하거나
```bash
./gradlew :desktop:shadowJar
```
로 shadowed JAR를 빌드하고 자바가 설치된 환경에서 `java -jar endless-dead-<빌드 날짜>.jar` 하면 된다.

#### 매개변수

`java [속성 정의] -jar endless-dead-<빌드 날짜>.jar [매개변수]`로 실행할 떄 지정할 수 있는 매개변수들이다.

속성 정의:
- `-Dzombie.worldSeed=<시드>`: 월드의 시드를 지정한다.
- `-Dzombie.instantGame=true`: 일회성 게임 모드로 실행한다.

매개변수: 아직 없다.

### Android

#### APK 빌드

PC 버전 빌드는 자바 1.8에서도 가능하지만 안드로이드 버전 빌드는 자바 17 이상이 요구된다.

`android` 디렉토리 내 `genkey.bat`를 실행하여 서명 키를 생성하고 `local.properties`를 만들어 키와 저장소의 비밀번호(`RELEASE_STORE_PASSWORD`, `RELEASE_KEY_PASSWORD`)를 등록한다.

그 다음 `buildapk.bat`를 실행하여 APK를 빌드한다. 빌드 시 도스창에 D8 관련(메타데이타 읽기 실패) 에러 폭탄이 뜰 수 있는데 그냥 무시해도 정상적으로 진행된다.

## 조작법

| 글쇠 | 동작 |
|---|---|
| 화살표 / WASD | 플레이어 이동 |
| Esc 또는 P | 일시 중지 |
| 좌클릭 | 아이템 사용 |
| 우클릭 / 사이띄개 | 상자 상호작용 |
| 휠 | 인벤토리 아이템 선택 |
| Delete | 선택된 아이템 삭제 |
| \` | 아이템 버리기 |
| Shift | 감속 |

## 프로젝트 구조

```
/
├── gdxhelper/   도우미 라이브러리
├── core/        게임 코드 (플랫폼 독립)
└── desktop/     PC용 실행기
```
