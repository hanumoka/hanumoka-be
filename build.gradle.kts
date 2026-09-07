// 이 파일은 비어 있다. 공통 설정은 buildSrc/ 의 관례 플러그인 셋이 갖는다.
//
//   hanumoka.kotlin-base     Kotlin · 툴체인 25 · 테스트 러너 · 층 의존 규칙 검사
//   hanumoka.spring-library  service-* / platform-*  — Boot 플러그인 없음, BOM 을 직접 가져온다
//   hanumoka.spring-app      app/*                   — Boot 플러그인, 실행 가능 jar
//
// ★ 여기에 allprojects·subprojects 블록을 두지 않는다.
//   한 프로젝트가 다른 프로젝트를 설정하면 Gradle 이 모듈을 독립적으로 다룰 수 없게 되고
//   설정 캐시와 병렬 실행이 막힌다. 공통 설정은 관례 플러그인으로 「적용」한다.
//
// 플러그인 버전은 buildSrc/build.gradle.kts 한 곳에만 있다.
