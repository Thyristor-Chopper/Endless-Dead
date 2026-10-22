# Endless Dead

코틀린으로 만든 libGDX 기반의 좀비 사냥 슈팅 게임이다.

---

## 요구 사항

자바 1.8 이상 (직접 빌드 시에는 JDK 필요)

Windows XP 32비트 SP3(JDK 1.8u51), Windows 7 64비트 SP1(JDK 17)에서 빌드 가능 확인.

Windows XP 32비트 SP3, Windows 7 64비트 SP1, Windows 10 64비트 LTSC 2021에서 정상 실행 확인.

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
로 shadowed JAR를 빌드하고 자바가 설치된 환경에서 `java -jar endless-dead-<빌드 날짜>.jar` 하면 된다.

### 매개변수

`java [속성 정의] -jar endless-dead-<빌드 날짜>.jar [매개변수]`로 실행할 떄 지정할 수 있는 매개변수들이다.

속성 정의:
- `-Dzombie.worldSeed=<시드>`: 월드의 시드를 지정한다.
- `-Dzombie.instantGame=true`: 일회성 게임 모드로 실행한다.

매개변수: 아직 없다.

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
