/**
 * `app` 층 모듈이 쓰는 관례. **프로세스가 되는 모듈이다.**
 *
 * 여기만 Boot 플러그인을 적용하고 `main` 을 가지며 실행 가능 jar 를 낸다.
 * 업무 규칙은 한 줄도 없다 — 하는 일은 셋뿐이다.
 * `main` 을 갖고 프로세스를 띄우고, 어떤 도메인을 넣을지 고르고, 환경 설정을 갖는다.
 */
plugins {
    id("hanumoka.kotlin-base")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // ★ 층 의존 규칙의 패키지 단위 검사.
    //   checkLayerDependencies 는 「모듈이 모듈을 의존하는가」를 보고,
    //   ArchUnit 은 「클래스가 클래스를 참조하는가」를 본다.
    //   같은 모듈 안에서 패키지를 잘못 쓰면 앞의 검사는 아무 말도 못 한다.
    //   이 층만 모든 도메인을 classpath 에 갖고 있어 전체 그래프를 볼 수 있다.
    testImplementation("com.tngtech.archunit:archunit-junit5:1.5.0")
}
