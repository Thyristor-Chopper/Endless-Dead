# Endless Dead

libGDX 기반의 좀비 사냥 슈팅 게임과 프레임워크가 포함되어 있다.

---

## 1. 요구 사항

JDK 1.8 이상 (Windows XP의 경우)

Windows 7 이상이라면 JDK 17 이상을 추천함.

---

## 2. 게임 실행

### 직접 실행

Windows는 `run.bat` 실행

매킨토시나 리눅스는
```bash
./gradlew desktop:run        # macOS / Linux
```

### JAR 사용

`build.bat`를 실행하거나

```bash
./gradlew :desktop:shadowJar
```
로 JAR를 생성하고 `java -jar endless-dead.jar` 하면 된다.

---

## 3. 조작법

| 글쇠 | 동작 |
|---|---|
| 화살표 또는 WASD | 플레이어 이동 |
| ESC | 일시 중지 |
| 좌클릭 | 아이템 사용 |
| 우클릭 또는 사이띄개 | 상자 상호작용 |

---

## 4. 프로젝트 구조

```
/
├── gdxhelper/   프레임워크
├── core/        게임 코드 (플랫폼 독립)
└── desktop/     PC용 실행기
```
