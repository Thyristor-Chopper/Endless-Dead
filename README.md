# Endless Dead & GDX Helper

코틀린으로 만든 libGDX 기반의 좀비 사냥 슈팅 게임 'Endless Dead'와 libGDX 프레임워크 'GDX helper'가 포함되어 있다.

gdxhelper는 자바와의 상호운용성을 최대한으로 고려하여 작성하고 자바 친화적 API도 별도로(이중으로) 제공하기 때문에 자바에서도 큰 문제 없이 자연스럽게 사용 가능하다.

---

## 1. 요구 사항

자바 1.8 이상 (Windows XP의 경우)

Windows 7 이상이라면 자바 17 이상을 추천함.

직접 빌드 시 JDK가 필요함.

## 2. 게임 실행

### 직접 실행

Windows는 `run.bat` 실행

매킨토시나 리눅스는
```bash
./gradlew desktop:run
```

### 컴파일된 JAR 사용

`build.bat`를 실행하거나

```bash
./gradlew :desktop:shadowJar
```
로 shadowed JAR를 빌드하고 `java -jar endless-dead.jar` 하면 된다.

## 3. 조작법

| 글쇠 | 동작 |
|---|---|
| 화살표 / WASD | 플레이어 이동 |
| Esc 또는 P | 일시 중지 |
| 좌클릭 | 아이템 사용 |
| 우클릭 / 사이띄개 | 상자 상호작용 |

## 4. 프로젝트 구조

```
/
├── gdxhelper/   프레임워크
├── core/        게임 코드 (플랫폼 독립)
└── desktop/     PC용 실행기
```
